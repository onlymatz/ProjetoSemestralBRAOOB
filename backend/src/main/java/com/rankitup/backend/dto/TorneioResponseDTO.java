package com.rankitup.backend.dto;

import com.rankitup.backend.model.Torneio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TorneioResponseDTO(
        Long idTorneio,
        String nome,
        BigDecimal premiacaoTotal,
        LocalDateTime dataCriacao,
        JogoResumoDTO jogo,
        UsuarioResumoDTO criador
) {
    public static TorneioResponseDTO from(Torneio torneio) {
        if (torneio == null) {
            return null;
        }

        return new TorneioResponseDTO(
                torneio.getIdTorneio(),
                torneio.getNome(),
                torneio.getPremiacaoTotal(),
                torneio.getDataCriacao(),
                JogoResumoDTO.from(torneio.getJogo()),
                UsuarioResumoDTO.from(torneio.getCriador())
        );
    }
}
