package com.rankitup.backend.dto;

import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.enums.StatusInscricao;

public record InscricaoResponseDTO(
        Long idInscricao,
        TorneioResponseDTO torneio,
        JogadorResumoDTO jogador,
        EquipeResumoDTO equipe,
        StatusInscricao status,
        Integer pontosAcumulados,
        Integer vitoriasTotais,
        Integer partidasTotais,
        Integer saldoKills
) {
    public static InscricaoResponseDTO from(Inscricao inscricao) {
        if (inscricao == null) {
            return null;
        }

        return new InscricaoResponseDTO(
                inscricao.getIdInscricao(),
                TorneioResponseDTO.from(inscricao.getTorneio()),
                JogadorResumoDTO.from(inscricao.getJogador()),
                EquipeResumoDTO.from(inscricao.getEquipe()),
                inscricao.getStatus(),
                inscricao.getPontosAcumulados(),
                inscricao.getVitoriasTotais(),
                inscricao.getPartidasTotais(),
                inscricao.getSaldoKills()
        );
    }
}
