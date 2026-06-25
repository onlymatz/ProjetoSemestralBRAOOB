package com.rankitup.backend.service;

import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.enums.Resultado;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

    // Fator K dinâmico baseado no TOTAL DE PARTIDAS jogadas (não só vitórias)
    // Iniciantes (< 10 partidas): K=40 — mais volatilidade, sobem/descem rápido
    // Intermediários (10-30 partidas): K=32 — volatilidade média
    // Experientes (> 30 partidas): K=20 — mais estabilidade
    private int calcularFatorK(int totalPartidas) {
        if (totalPartidas < 10) return 40;
        if (totalPartidas < 30) return 32;
        return 20;
    }

    private double calcularProbabilidadeVitoria(int ratingA, int ratingB) {
        return 1.0 / (1.0 + Math.pow(10.0, (ratingB - ratingA) / 400.0));
    }

    public void processarDuelo(Inscricao inscricaoA, Inscricao inscricaoB, Resultado resultadoA) {

        int ratingA = valorOuZero(inscricaoA.getPontosAcumulados());
        int ratingB = valorOuZero(inscricaoB.getPontosAcumulados());

        // Fator K individual para cada jogador
        int partidasA = valorOuZero(inscricaoA.getPartidasTotais());
        int partidasB = valorOuZero(inscricaoB.getPartidasTotais());
        int vitoriasA = valorOuZero(inscricaoA.getVitoriasTotais());
        int vitoriasB = valorOuZero(inscricaoB.getVitoriasTotais());
        int kA = calcularFatorK(partidasA);
        int kB = calcularFatorK(partidasB);

        double expectativaA = calcularProbabilidadeVitoria(ratingA, ratingB);
        double expectativaB = calcularProbabilidadeVitoria(ratingB, ratingA);

        double placarA = converterResultadoParaPlacar(resultadoA);
        double placarB = converterResultadoParaPlacar(inverterResultado(resultadoA));

        int variacaoA = (int) Math.round(kA * (placarA - expectativaA));
        int variacaoB = (int) Math.round(kB * (placarB - expectativaB));

        inscricaoA.setPontosAcumulados(Math.max(0, ratingA + variacaoA));
        if (resultadoA == Resultado.VITORIA) {
            inscricaoA.setVitoriasTotais(vitoriasA + 1);
        }
        inscricaoA.setPartidasTotais(partidasA + 1);

        inscricaoB.setPontosAcumulados(Math.max(0, ratingB + variacaoB));
        if (inverterResultado(resultadoA) == Resultado.VITORIA) {
            inscricaoB.setVitoriasTotais(vitoriasB + 1);
        }
        inscricaoB.setPartidasTotais(partidasB + 1);
    }

    public Resultado inverterResultado(Resultado resultado) {
        return switch (resultado) {
            case VITORIA -> Resultado.DERROTA;
            case DERROTA -> Resultado.VITORIA;
            case EMPATE  -> Resultado.EMPATE;
        };
    }

    private double converterResultadoParaPlacar(Resultado resultado) {
        return switch (resultado) {
            case VITORIA -> 1.0;
            case EMPATE  -> 0.5;
            case DERROTA -> 0.0;
        };
    }

    private int valorOuZero(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
