package br.edu.tedi.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.edu.tedi.auth.dto.TokenDTO;
import br.edu.tedi.auth.dto.UsuarioLoginDTO;
import br.edu.tedi.auth.security.JwtService;
import br.edu.tedi.auth.security.TokenBlacklist;
import br.edu.tedi.auth.service.AutenticacaoService;
import br.edu.tedi.auth.service.TentativasLoginService;
import br.edu.tedi.common.exception.CredenciaisInvalidasException;
import br.edu.tedi.usuario.entity.Usuario;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;
    @Mock TokenBlacklist blacklist;
    @Mock TentativasLoginService tentativas;
    @InjectMocks AutenticacaoService service;

    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("ana@tedi.edu");
        usuario.setSenha("hash");
        usuario.setAtivo(true);
    }

    @Test
    void loginValidoRetornaTokens() {
        when(repository.findByEmailIgnoreCase("ana@tedi.edu")).thenReturn(Optional.of(usuario));
        when(encoder.matches("12345678", "hash")).thenReturn(true);
        when(jwtService.gerarAcesso(1)).thenReturn("acesso");
        when(jwtService.gerarRefresh(1)).thenReturn("refresh");
        when(jwtService.getAcessoSegundos()).thenReturn(900L);

        TokenDTO token = service.autenticar(new UsuarioLoginDTO("ana@tedi.edu", "12345678"));

        assertEquals("acesso", token.accessToken());
        assertEquals("refresh", token.refreshToken());
        verify(tentativas).limpar("ana@tedi.edu");
    }

    @Test
    void senhaIncorretaLancaCredenciaisInvalidas() {
        when(repository.findByEmailIgnoreCase("ana@tedi.edu")).thenReturn(Optional.of(usuario));
        when(encoder.matches("errada", "hash")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class,
                () -> service.autenticar(new UsuarioLoginDTO("ana@tedi.edu", "errada")));
        verify(tentativas).registrarFalha("ana@tedi.edu");
    }

    @Test
    void emailInexistenteLancaCredenciaisInvalidas() {
        when(repository.findByEmailIgnoreCase("x@x.com")).thenReturn(Optional.empty());

        assertThrows(CredenciaisInvalidasException.class,
                () -> service.autenticar(new UsuarioLoginDTO("x@x.com", "12345678")));
    }

    @Test
    void usuarioInativoNaoAutentica() {
        usuario.setAtivo(false);
        when(repository.findByEmailIgnoreCase("ana@tedi.edu")).thenReturn(Optional.of(usuario));

        assertThrows(CredenciaisInvalidasException.class,
                () -> service.autenticar(new UsuarioLoginDTO("ana@tedi.edu", "12345678")));
        verify(jwtService, never()).gerarAcesso(any());
    }
}
