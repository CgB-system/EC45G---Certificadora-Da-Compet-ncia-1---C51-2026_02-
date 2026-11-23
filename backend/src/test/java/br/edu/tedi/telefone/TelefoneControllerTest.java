package br.edu.tedi.telefone;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import br.edu.tedi.auth.security.UsuarioAutenticado;
import br.edu.tedi.common.exception.AcessoNegadoException;
import br.edu.tedi.telefone.controller.TelefoneController;
import br.edu.tedi.telefone.dto.TelefoneDTO;
import br.edu.tedi.telefone.service.TelefoneService;
import org.junit.jupiter.api.Test;

class TelefoneControllerTest {

    private final TelefoneService service = mock(TelefoneService.class);
    private final TelefoneController controller = new TelefoneController(service);

    @Test
    void usuarioPodeAcessarTelefonesDaPropriaConta() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(10, "usuario@tedi.edu", false);
        controller.listar(10, usuario);
        verify(service).listarTelefones(10);
    }

    @Test
    void usuarioNaoPodeAcessarTelefonesDeOutraConta() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(10, "usuario@tedi.edu", false);
        assertThrows(AcessoNegadoException.class, () -> controller.listar(11, usuario));
        verifyNoInteractions(service);
    }

    @Test
    void administradorPodeAcessarTelefonesDeOutraConta() {
        UsuarioAutenticado administrador = new UsuarioAutenticado(1, "credencial", true);
        controller.listar(10, administrador);
        verify(service).listarTelefones(10);
    }
}
