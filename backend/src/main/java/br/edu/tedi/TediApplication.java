package br.edu.tedi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class TediApplication {
    public static void main(String[] args) {
        SpringApplication.run(TediApplication.class, args);
    }
}
