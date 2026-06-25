package com.rankitup.backend.controller;

import com.rankitup.backend.dto.JogadorResumoDTO;
import com.rankitup.backend.model.Jogador;
import com.rankitup.backend.repository.EquipeRepository;
import com.rankitup.backend.repository.JogadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jogadores")
@RequiredArgsConstructor
public class JogadorController {

    private final JogadorRepository jogadorRepository;
    private final EquipeRepository equipeRepository;

    // Endpoint para buscar todos os jogadores cadastrados
    @GetMapping
    @Transactional(readOnly = true)
    public List<JogadorResumoDTO> listarTodos() {
        return jogadorRepository.findAll().stream()
                .map(JogadorResumoDTO::from)
                .toList();
    }

    // Endpoint para cadastrar um novo jogador
    @PostMapping
    @Transactional
    public ResponseEntity<JogadorResumoDTO> cadastrar(@RequestBody Jogador novoJogador) {
        if (novoJogador.getEquipe() != null && novoJogador.getEquipe().getIdEquipe() != null) {
            novoJogador.setEquipe(equipeRepository.findById(novoJogador.getEquipe().getIdEquipe())
                    .orElseThrow(() -> new IllegalArgumentException("Equipe nao encontrada.")));
        }

        Jogador jogadorSalvo = jogadorRepository.save(novoJogador);
        return ResponseEntity.ok(JogadorResumoDTO.from(jogadorSalvo));
    }
}
