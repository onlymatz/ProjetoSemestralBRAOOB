package com.rankitup.backend.controller;

import com.rankitup.backend.dto.InscricaoResponseDTO;
import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.enums.StatusInscricao;
import com.rankitup.backend.repository.EquipeRepository;
import com.rankitup.backend.repository.InscricaoRepository;
import com.rankitup.backend.repository.JogadorRepository;
import com.rankitup.backend.repository.TorneioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoRepository inscricaoRepository;
    private final TorneioRepository torneioRepository;
    private final JogadorRepository jogadorRepository;
    private final EquipeRepository equipeRepository;

    // Lista todas as inscrições — serve como tabela de ranking
    @GetMapping
    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> listarTodas() {
        return inscricaoRepository.findAll().stream()
                .map(InscricaoResponseDTO::from)
                .toList();
    }

    // Lista inscrições de um torneio filtradas por status
    // GET /api/inscricoes/torneio/1?status=PENDENTE
    @GetMapping("/torneio/{idTorneio}")
    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> listarPorTorneioEStatus(
            @PathVariable Long idTorneio,
            @RequestParam(defaultValue = "APROVADO") StatusInscricao status) {
        return inscricaoRepository.findByTorneio_IdTorneioAndStatus(idTorneio, status).stream()
                .map(InscricaoResponseDTO::from)
                .toList();
    }

    // Jogador solicita inscrição — começa como PENDENTE automaticamente (RGN-04)
    @PostMapping
    @Transactional
    public ResponseEntity<InscricaoResponseDTO> inscrever(@RequestBody Inscricao novaInscricao) {
        if (novaInscricao.getTorneio() == null || novaInscricao.getTorneio().getIdTorneio() == null) {
            throw new IllegalArgumentException("Torneio nao informado.");
        }
        if (novaInscricao.getJogador() == null || novaInscricao.getJogador().getIdUsuario() == null) {
            throw new IllegalArgumentException("Jogador nao informado.");
        }

        novaInscricao.setTorneio(torneioRepository.findById(novaInscricao.getTorneio().getIdTorneio())
                .orElseThrow(() -> new IllegalArgumentException("Torneio nao encontrado.")));
        novaInscricao.setJogador(jogadorRepository.findById(novaInscricao.getJogador().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Jogador nao encontrado.")));

        if (novaInscricao.getEquipe() != null && novaInscricao.getEquipe().getIdEquipe() != null) {
            novaInscricao.setEquipe(equipeRepository.findById(novaInscricao.getEquipe().getIdEquipe())
                    .orElseThrow(() -> new IllegalArgumentException("Equipe nao encontrada.")));
        }

        novaInscricao.setStatus(StatusInscricao.PENDENTE);
        return ResponseEntity.ok(InscricaoResponseDTO.from(inscricaoRepository.save(novaInscricao)));
    }

    // Admin aprova ou rejeita uma inscrição
    // PATCH /api/inscricoes/1/status?status=APROVADO
    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<InscricaoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusInscricao status) {

        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada."));

        inscricao.setStatus(status);
        return ResponseEntity.ok(InscricaoResponseDTO.from(inscricaoRepository.save(inscricao)));
    }
}
