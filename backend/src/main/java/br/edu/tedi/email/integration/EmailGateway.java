package br.edu.tedi.email.integration;

import br.edu.tedi.email.dto.EmailMensagem;

public interface EmailGateway {

    void enviar(EmailMensagem mensagem);
}
