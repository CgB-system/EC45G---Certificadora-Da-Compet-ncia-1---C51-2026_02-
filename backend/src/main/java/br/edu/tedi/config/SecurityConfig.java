package br.edu.tedi.config;

import br.edu.tedi.administrador.repository.AdministradorRepository;
import br.edu.tedi.auth.security.JwtAuthFilter;
import br.edu.tedi.auth.security.JwtService;
import br.edu.tedi.auth.security.RespostaErroSeguranca;
import br.edu.tedi.auth.security.TokenBlacklist;
import br.edu.tedi.usuario.repository.UsuarioRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, TokenBlacklist blacklist,
                                           UsuarioRepository usuarioRepository,
                                           AdministradorRepository administradorRepository,
                                           RespostaErroSeguranca erroSeguranca) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                        .requestMatchers("/", "/index.html", "/pages/**", "/assets/**", "/components/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/v1/auth/**").authenticated()
                        .requestMatchers("/api/v1/usuarios/*/telefones/**").authenticated()
                        .requestMatchers("/api/v1/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(erroSeguranca).accessDeniedHandler(erroSeguranca))
                .addFilterBefore(new JwtAuthFilter(jwtService, blacklist, usuarioRepository, administradorRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${tedi.cors.origens:http://localhost:5173,http://localhost:3000}") String[] origens) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(origens));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", config);
        return fonte;
    }
}
