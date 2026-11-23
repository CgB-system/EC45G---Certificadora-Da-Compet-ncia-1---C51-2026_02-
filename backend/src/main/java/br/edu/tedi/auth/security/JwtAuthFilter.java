package br.edu.tedi.auth.security;

import br.edu.tedi.administrador.repository.AdministradorRepository;
import br.edu.tedi.usuario.entity.Usuario;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final TokenBlacklist blacklist;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public JwtAuthFilter(JwtService jwtService, TokenBlacklist blacklist,
                         UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.jwtService = jwtService;
        this.blacklist = blacklist;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String cabecalho = req.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            autenticar(cabecalho.substring(7));
        }
        chain.doFilter(req, res);
    }

    private void autenticar(String token) {
        try {
            Claims claims = jwtService.ler(token);
            if (!"access".equals(claims.get("tipo", String.class)) || blacklist.estaRevogado(claims.getId())) {
                return;
            }
            Integer id = Integer.valueOf(claims.getSubject());
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null || !Boolean.TRUE.equals(usuario.getAtivo())) {
                return;
            }
            boolean admin = administradorRepository.existsById(id);
            List<SimpleGrantedAuthority> perfis = admin
                    ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                    : List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsuarioAutenticado principal = new UsuarioAutenticado(id, usuario.getEmail(), admin);
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, perfis));
        } catch (RuntimeException e) {
            log.debug("Token rejeitado: {}", e.getMessage());
        }
    }
}
