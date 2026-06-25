package com.rankitup.backend.controller;

import com.rankitup.backend.dto.TorneioResponseDTO;
import com.rankitup.backend.model.Administrador;
import com.rankitup.backend.model.Torneio;
import com.rankitup.backend.repository.AdministradorRepository;
import com.rankitup.backend.repository.JogoRepository;
import com.rankitup.backend.repository.TorneioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneios")
@RequiredArgsConstructor
public class TorneioController {

    private final TorneioRepository torneioRepository;
    private final AdministradorRepository administradorRepository;
    private final JogoRepository jogoRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public List<TorneioResponseDTO> listarTodos() {
        return torneioRepository.findAll().stream()
                .map(TorneioResponseDTO::from)
                .toList();
    }

    // Ao criar o torneio, vincula automaticamente o admin autenticado como criador
    @PostMapping
    @Transactional
    public ResponseEntity<TorneioResponseDTO> cadastrar(@RequestBody Torneio novoTorneio,
                                                        Authentication authentication) {

        // Pega o email do admin logado direto do token JWT
        String emailAdmin = authentication.getName();

        Administrador criador = administradorRepository.findByEmail(emailAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador não encontrado."));

        if (novoTorneio.getJogo() == null || novoTorneio.getJogo().getIdJogo() == null) {
            throw new IllegalArgumentException("Jogo nao informado.");
        }

        novoTorneio.setCriador(criador);
        novoTorneio.setJogo(jogoRepository.findById(novoTorneio.getJogo().getIdJogo())
                .orElseThrow(() -> new IllegalArgumentException("Jogo nao encontrado.")));

        return ResponseEntity.ok(TorneioResponseDTO.from(torneioRepository.save(novoTorneio)));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TorneioResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody Torneio dadosAtualizados,
                                                        Authentication authentication) {
        Torneio torneio = torneioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneio nao encontrado."));

        String emailAdmin = authentication.getName();
        if (!torneio.getCriador().getEmail().equals(emailAdmin)) {
            throw new SecurityException("Apenas o criador pode editar este torneio.");
        }

        if (dadosAtualizados.getNome() != null && !dadosAtualizados.getNome().isBlank()) {
            torneio.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getPremiacaoTotal() != null) {
            torneio.setPremiacaoTotal(dadosAtualizados.getPremiacaoTotal());
        }
        if (dadosAtualizados.getJogo() != null && dadosAtualizados.getJogo().getIdJogo() != null) {
            torneio.setJogo(jogoRepository.findById(dadosAtualizados.getJogo().getIdJogo())
                    .orElseThrow(() -> new IllegalArgumentException("Jogo nao encontrado.")));
        }

        return ResponseEntity.ok(TorneioResponseDTO.from(torneioRepository.save(torneio)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> excluir(@PathVariable Long id, Authentication authentication) {
        Torneio torneio = torneioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneio nao encontrado."));

        String emailAdmin = authentication.getName();
        if (!torneio.getCriador().getEmail().equals(emailAdmin)) {
            throw new SecurityException("Apenas o criador pode excluir este torneio.");
        }

        torneioRepository.deleteById(id);
        return ResponseEntity.ok("Torneio excluido com sucesso.");
    }
}
