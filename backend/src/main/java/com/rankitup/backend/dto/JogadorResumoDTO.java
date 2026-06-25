package com.rankitup.backend.dto;

import com.rankitup.backend.model.Jogador;
import com.rankitup.backend.model.enums.PerfilUsuario;

public record JogadorResumoDTO(
        Long idUsuario,
        String email,
        PerfilUsuario perfil,
        String nome,
        String nickname,
        String fotoPerfil,
        EquipeResumoDTO equipe
) {
    public static JogadorResumoDTO from(Jogador jogador) {
        if (jogador == null) {
            return null;
        }

        return new JogadorResumoDTO(
                jogador.getIdUsuario(),
                jogador.getEmail(),
                jogador.getPerfil(),
                jogador.getNome(),
                jogador.getNickname(),
                jogador.getFotoPerfil(),
                EquipeResumoDTO.from(jogador.getEquipe())
        );
    }
}
