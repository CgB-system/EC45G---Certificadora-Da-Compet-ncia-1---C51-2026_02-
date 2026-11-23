package br.edu.tedi.gerenciador;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.common.exception.AcessoNegadoException;
import br.edu.tedi.gerenciador.controller.GerenciadorController;
import br.edu.tedi.gerenciador.dto.AtribuirVoluntarioDTO;
import br.edu.tedi.gerenciador.service.GerenciadorService;
import org.junit.jupiter.api.Test;

class GerenciadorControllerTest {

    private final GerenciadorService service = mock(GerenciadorService.class);
    private final GerenciadorController controller = new GerenciadorController(service);

    @Test
    void administradorSoPodeAtribuirEmSeuProprioNome() {
        UsuarioAutenticado administrador = new UsuarioAutenticado(1, "credencial", true);
        controller.atribuir(new AtribuirVoluntarioDTO(1, 20, null), administrador);
        verify(service).atribuirVoluntario(new AtribuirVoluntarioDTO(1, 20, null));
    }

    @Test
    void usuarioComumNaoPodeAtribuirGerenciamento() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(1, "usuario@tedi.edu", false);
        assertThrows(AcessoNegadoException.class,
                () -> controller.atribuir(new AtribuirVoluntarioDTO(1, 20, null), usuario));
        verifyNoInteractions(service);
    }

    @Test
    void administradorNaoPodeInformarOutroAdministrador() {
        UsuarioAutenticado administrador = new UsuarioAutenticado(1, "credencial", true);
        assertThrows(AcessoNegadoException.class,
                () -> controller.atribuir(new AtribuirVoluntarioDTO(2, 20, null), administrador));
        verifyNoInteractions(service);
    }
}
