package com.rankitup.backend.dto;

import com.rankitup.backend.model.Jogo;
import com.rankitup.backend.model.enums.GeneroJogo;

public record JogoResumoDTO(
        Long idJogo,
        String titulo,
        GeneroJogo genero,
        String desenvolvedora
) {
    public static JogoResumoDTO from(Jogo jogo) {
        if (jogo == null) {
            return null;
        }

        return new JogoResumoDTO(
                jogo.getIdJogo(),
                jogo.getTitulo(),
                jogo.getGenero(),
                jogo.getDesenvolvedora()
        );
    }
}
