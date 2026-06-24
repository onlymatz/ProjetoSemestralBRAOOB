package com.rankitup.backend.service;

import com.rankitup.backend.dto.AtualizarUsuarioDTO;
import com.rankitup.backend.dto.CadastroUsuarioDTO;
import com.rankitup.backend.dto.LoginDTO;
import com.rankitup.backend.model.Administrador;
import com.rankitup.backend.model.Jogador;
import com.rankitup.backend.model.Usuario;
import com.rankitup.backend.model.enums.PerfilUsuario;
import com.rankitup.backend.repository.JogadorRepository;
import com.rankitup.backend.repository.UsuarioRepository;
import com.rankitup.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JogadorRepository jogadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Usuario cadastrar(CadastroUsuarioDTO dto) {
        validarCampo(dto.email(), "E-mail");
        validarCampo(dto.senha(), "Senha");
        validarCampo(dto.perfil(), "Perfil");

        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail ja cadastrado.");
        }

        PerfilUsuario perfil;
        try {
            perfil = PerfilUsuario.valueOf(dto.perfil());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Perfil de usuario invalido.");
        }

        Usuario novoUsuario = switch (perfil) {
            case ROLE_ADMIN, ROLE_SUPORTE -> new Administrador();
            case ROLE_USER -> {
                validarCampo(dto.nome(), "Nome");
                validarCampo(dto.nickname(), "Nickname");
                if (jogadorRepository.findByNickname(dto.nickname()).isPresent()) {
                    throw new IllegalArgumentException("Nickname ja cadastrado.");
                }

                Jogador jogador = new Jogador();
                jogador.setNome(dto.nome());
                jogador.setNickname(dto.nickname());
                yield jogador;
            }
        };

        novoUsuario.setEmail(dto.email());
        novoUsuario.setPerfil(perfil);
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));

        return usuarioRepository.save(novoUsuario);
    }

    public String login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("E-mail nao encontrado."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return jwtService.gerarToken(usuario.getEmail(), usuario.getPerfil().name());
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
    }

    public Usuario atualizar(Long id, AtualizarUsuarioDTO dto, String emailRequisitante) {
        Usuario usuario = buscarPorId(id);

        Usuario requisitante = usuarioRepository.findByEmail(emailRequisitante)
                .orElseThrow(() -> new IllegalArgumentException("Requisitante nao encontrado."));

        boolean isAdmin = requisitante.getPerfil() == PerfilUsuario.ROLE_ADMIN;
        boolean isSelf = usuario.getEmail().equals(emailRequisitante);

        if (!isAdmin && !isSelf) {
            throw new SecurityException("Sem permissao para atualizar este usuario.");
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        if (usuario instanceof Jogador jogador) {
            if (dto.nome() != null && !dto.nome().isBlank()) {
                jogador.setNome(dto.nome());
            }
            if (dto.nickname() != null && !dto.nickname().isBlank()) {
                jogadorRepository.findByNickname(dto.nickname()).ifPresent(outroJogador -> {
                    if (!outroJogador.getIdUsuario().equals(jogador.getIdUsuario())) {
                        throw new IllegalArgumentException("Nickname ja cadastrado.");
                    }
                });
                jogador.setNickname(dto.nickname());
            }
            if (dto.fotoPerfil() != null && !dto.fotoPerfil().isBlank()) {
                jogador.setFotoPerfil(dto.fotoPerfil());
            }
        }

        return usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario nao encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public boolean verificarSenha(String senhaTexto, String hashArmazenado) {
        return passwordEncoder.matches(senhaTexto, hashArmazenado);
    }

    private void validarCampo(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio.");
        }
    }
}
