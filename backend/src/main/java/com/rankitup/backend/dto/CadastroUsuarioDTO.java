package com.rankitup.backend.dto;

public record CadastroUsuarioDTO(
    String email,
    String senha,
    String perfil,
    String nome,
    String nickname
) { }
