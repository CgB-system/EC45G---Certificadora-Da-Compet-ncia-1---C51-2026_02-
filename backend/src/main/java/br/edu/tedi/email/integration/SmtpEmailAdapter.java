package br.edu.tedi.email.integration;

import br.edu.tedi.common.exception.IntegracaoExternaException;
import br.edu.tedi.email.dto.EmailMensagem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailAdapter implements EmailGateway {

    private final JavaMailSender mailSender;
    private final String remetente;

    public SmtpEmailAdapter(JavaMailSender mailSender, @Value("${tedi.email.remetente:}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @Override
    public void enviar(EmailMensagem mensagem) {
        if (remetente == null || remetente.isBlank()) {
            throw new IntegracaoExternaException("Remetente de e-mail não configurado.");
        }
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(mensagem.destinatario());
            helper.setSubject(mensagem.assunto());
            helper.setText(mensagem.corpo(), false);
            mailSender.send(mime);
        } catch (MailException | MessagingException e) {
            throw new IntegracaoExternaException("Falha ao enviar e-mail pelo servidor SMTP.", e);
        }
    }
}
