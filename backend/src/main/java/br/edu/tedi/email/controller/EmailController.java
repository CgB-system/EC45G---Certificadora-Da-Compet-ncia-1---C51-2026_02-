package br.edu.tedi.email.controller;

import br.edu.tedi.email.dto.EnvioEmailDTO;
import br.edu.tedi.email.dto.EnvioFormularioDTO;
import br.edu.tedi.email.dto.ResultadoEnvioDTO;
import br.edu.tedi.email.service.EmailService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) { this.service = service; }

    @PostMapping("/enviar-formulario")
    public List<ResultadoEnvioDTO> enviarFormulario(@Valid @RequestBody EnvioFormularioDTO dto) {
        return service.enviarFormulario(dto);
    }

    @PostMapping("/enviar")
    public List<ResultadoEnvioDTO> enviar(@Valid @RequestBody EnvioEmailDTO dto) {
        return service.enviarEmail(dto);
    }
}
