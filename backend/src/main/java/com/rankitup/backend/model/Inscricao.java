package com.rankitup.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rankitup.backend.model.enums.StatusInscricao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Inscricao", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_torneio", "id_jogador"})
})
@Getter
@Setter
@NoArgsConstructor
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscricao")
    private Long idInscricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_torneio", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Torneio torneio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jogador", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Jogador jogador;

    // Problema 10 — RGN-09: jogador só pode estar em uma equipe por torneio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipe")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Equipe equipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusInscricao status = StatusInscricao.PENDENTE;

    @Column(name = "pontos_acumulados")
    private Integer pontosAcumulados = 0;

    @Column(name = "vitorias_totais")
    private Integer vitoriasTotais = 0;

    @Column(name = "partidas_totais")
    private Integer partidasTotais = 0;

    @Column(name = "saldo_kills")
    private Integer saldoKills = 0;
}