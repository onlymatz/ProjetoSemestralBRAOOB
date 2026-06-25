package com.rankitup.backend.dto;

import com.rankitup.backend.model.Equipe;

public record EquipeResumoDTO(
        Long idEquipe,
        String nomeEquipe,
        String tagEquipe,
        String paisOrigem
) {
    public static EquipeResumoDTO from(Equipe equipe) {
        if (equipe == null) {
            return null;
        }

        return new EquipeResumoDTO(
                equipe.getIdEquipe(),
                equipe.getNomeEquipe(),
                equipe.getTagEquipe(),
                equipe.getPaisOrigem()
        );
    }
}
