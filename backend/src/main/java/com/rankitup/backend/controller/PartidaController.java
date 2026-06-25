package com.rankitup.backend.controller;

import com.rankitup.backend.dto.ResultadoPartidaDTO;
import com.rankitup.backend.model.DesempenhoPartida;
import com.rankitup.backend.model.DesempenhoPartidaId;
import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.Partida;
import com.rankitup.backend.model.enums.Resultado;
import com.rankitup.backend.model.enums.StatusInscricao;
import com.rankitup.backend.repository.DesempenhoPartidaRepository;
import com.rankitup.backend.repository.InscricaoRepository;
import com.rankitup.backend.repository.PartidaRepository;
import com.rankitup.backend.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidas")
@RequiredArgsConstructor
public class PartidaController {

    private final PartidaRepository partidaRepository;
    private final InscricaoRepository inscricaoRepository;
    private final RankingService rankingService;
    private final DesempenhoPartidaRepository desempenhoRepository;

    @GetMapping
    public List<Partida> listarTodas() {
        return partidaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Partida> cadastrar(@RequestBody Partida novaPartida) {
        return ResponseEntity.ok(partidaRepository.save(novaPartida));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody Partida dadosAtualizados,
                                       Authentication authentication) {

        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada."));

        // RGN-07: só o criador do torneio pode editar
        String emailAdmin   = authentication.getName();
        String emailCriador = partida.getTorneio().getCriador().getEmail();
        if (!emailAdmin.equals(emailCriador)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o organizador deste torneio pode editar partidas.");
        }

        if (dadosAtualizados.getFaseTorneio() != null) {
            partida.setFaseTorneio(dadosAtualizados.getFaseTorneio());
        }

        return ResponseEntity.ok(partidaRepository.save(partida));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id,
                                     Authentication authentication) {

        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada."));

        // RGN-07: só o criador do torneio pode excluir
        String emailAdmin   = authentication.getName();
        String emailCriador = partida.getTorneio().getCriador().getEmail();
        if (!emailAdmin.equals(emailCriador)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o organizador deste torneio pode excluir partidas.");
        }

        // Busca os dois participantes pelo banco — sem precisar de body na requisição
        List<DesempenhoPartida> desempenhos = desempenhoRepository.findByPartida_IdPartida(id);

        if (desempenhos.size() == 2) {
            DesempenhoPartida dpA = desempenhos.get(0);
            DesempenhoPartida dpB = desempenhos.get(1);

            Inscricao inscricaoA = inscricaoRepository.findById(dpA.getInscricao().getIdInscricao())
                    .orElseThrow(() -> new IllegalArgumentException("Inscrição A não encontrada."));
            Inscricao inscricaoB = inscricaoRepository.findById(dpB.getInscricao().getIdInscricao())
                    .orElseThrow(() -> new IllegalArgumentException("Inscrição B não encontrada."));

            // Reverte o Elo invertendo o resultado original do jogador A
            rankingService.processarDuelo(inscricaoA, inscricaoB,
                    rankingService.inverterResultado(dpA.getResultado()));

            inscricaoRepository.saveAll(List.of(inscricaoA, inscricaoB));
        }

        desempenhoRepository.deleteAll(desempenhos);
        partidaRepository.deleteById(id);

        return ResponseEntity.ok("Partida excluída e rankings revertidos.");
    }

    // Registra o resultado e atualiza o Elo de ambos na mesma transação
    @Transactional
    @PostMapping("/{id}/resultado")
    public ResponseEntity<?> registrarResultado(@PathVariable Long id,
                                                @RequestBody ResultadoPartidaDTO dto,
                                                Authentication authentication) {

        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada."));

        String emailAdmin   = authentication.getName();
        String emailCriador = partida.getTorneio().getCriador().getEmail();
        if (!emailAdmin.equals(emailCriador)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o organizador deste torneio pode registrar resultados.");
        }

        Inscricao inscricaoA = inscricaoRepository.findById(dto.idInscricaoA())
                .orElseThrow(() -> new IllegalArgumentException("Inscrição do jogador A não encontrada."));
        Inscricao inscricaoB = inscricaoRepository.findById(dto.idInscricaoB())
                .orElseThrow(() -> new IllegalArgumentException("Inscrição do jogador B não encontrada."));

        if (dto.idInscricaoA().equals(dto.idInscricaoB())) {
            return ResponseEntity.badRequest().body("Um jogador não pode enfrentar a si mesmo.");
        }

        if (inscricaoA.getStatus() != StatusInscricao.APROVADO) {
            return ResponseEntity.badRequest().body("Jogador A não tem inscrição aprovada neste torneio.");
        }
        if (inscricaoB.getStatus() != StatusInscricao.APROVADO) {
            return ResponseEntity.badRequest().body("Jogador B não tem inscrição aprovada neste torneio.");
        }

        if (!inscricaoA.getTorneio().getIdTorneio().equals(partida.getTorneio().getIdTorneio())
                || !inscricaoB.getTorneio().getIdTorneio().equals(partida.getTorneio().getIdTorneio())) {
            return ResponseEntity.badRequest().body("Os jogadores precisam estar inscritos no torneio desta partida.");
        }
        if (!desempenhoRepository.findByPartida_IdPartida(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esta partida ja possui resultado registrado.");
        }

        rankingService.processarDuelo(inscricaoA, inscricaoB, dto.resultadoA());
        inscricaoRepository.saveAll(List.of(inscricaoA, inscricaoB));
        desempenhoRepository.saveAll(List.of(
                criarDesempenho(partida, inscricaoA, dto.resultadoA()),
                criarDesempenho(partida, inscricaoB, rankingService.inverterResultado(dto.resultadoA()))
        ));

        return ResponseEntity.ok("Resultado registrado e rankings atualizados.");
    }

    private DesempenhoPartida criarDesempenho(Partida partida, Inscricao inscricao, Resultado resultado) {
        DesempenhoPartida desempenho = new DesempenhoPartida();
        DesempenhoPartidaId desempenhoId = new DesempenhoPartidaId();
        desempenhoId.setIdPartida(partida.getIdPartida());
        desempenhoId.setIdInscricao(inscricao.getIdInscricao());
        desempenho.setId(desempenhoId);
        desempenho.setPartida(partida);
        desempenho.setInscricao(inscricao);
        desempenho.setResultado(resultado);
        desempenho.setScoreIndividual(resultado == Resultado.VITORIA ? 1 : 0);
        return desempenho;
    }
}
