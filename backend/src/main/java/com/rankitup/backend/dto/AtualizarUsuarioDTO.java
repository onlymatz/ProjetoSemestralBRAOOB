package com.rankitup.backend.dto;

public record AtualizarUsuarioDTO(
        String nome,
        String nickname,
        String fotoPerfil,
        String senha
) {}
