package com.rankitup.backend.config;

import com.rankitup.backend.model.Administrador;
import com.rankitup.backend.model.enums.PerfilUsuario;
import com.rankitup.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${rankitup.admin.email:}")
    private String adminEmail;

    @Value("${rankitup.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }

        if (usuarioRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Administrador admin = new Administrador();
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode(adminPassword));
        admin.setPerfil(PerfilUsuario.ROLE_ADMIN);
        usuarioRepository.save(admin);
    }
}
