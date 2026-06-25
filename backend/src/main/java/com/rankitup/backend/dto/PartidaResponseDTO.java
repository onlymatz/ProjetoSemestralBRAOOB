package com.rankitup.backend.dto;

import com.rankitup.backend.model.Partida;

import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long idPartida,
        TorneioResponseDTO torneio,
        LocalDateTime dataRegistro,
        String faseTorneio
) {
    public static PartidaResponseDTO from(Partida partida) {
        if (partida == null) {
            return null;
        }

        return new PartidaResponseDTO(
                partida.getIdPartida(),
                TorneioResponseDTO.from(partida.getTorneio()),
                partida.getDataRegistro(),
                partida.getFaseTorneio()
        );
    }
}
