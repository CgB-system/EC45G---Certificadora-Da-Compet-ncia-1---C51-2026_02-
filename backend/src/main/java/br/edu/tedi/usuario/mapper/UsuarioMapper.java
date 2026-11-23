package br.edu.tedi.usuario.mapper;

import br.edu.tedi.administrador.entity.Administrador;
import br.edu.tedi.usuario.dto.UsuarioResponseDTO;
import br.edu.tedi.usuario.entity.Usuario;
import br.edu.tedi.voluntario.entity.Voluntario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponse(Usuario u) {
        String tipo = u instanceof Administrador ? "ADMINISTRADOR" : u instanceof Voluntario ? "VOLUNTARIO" : "USUARIO";
        return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getSobrenome(), u.getEmail(), u.getAtivo(), tipo);
    }
}
