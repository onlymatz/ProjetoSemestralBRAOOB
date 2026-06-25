package com.rankitup.backend.controller;

import com.rankitup.backend.model.Jogo;
import com.rankitup.backend.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jogos")
@RequiredArgsConstructor
public class JogoController {

    private final JogoRepository jogoRepository;

    @GetMapping
    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Jogo> cadastrar(@RequestBody Jogo novoJogo) {
        return ResponseEntity.ok(jogoRepository.save(novoJogo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizar(@PathVariable Long id, @RequestBody Jogo dadosAtualizados) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Jogo nao encontrado."));

        if (dadosAtualizados.getTitulo() != null && !dadosAtualizados.getTitulo().isBlank()) {
            jogo.setTitulo(dadosAtualizados.getTitulo());
        }
        if (dadosAtualizados.getGenero() != null) {
            jogo.setGenero(dadosAtualizados.getGenero());
        }
        jogo.setDesenvolvedora(dadosAtualizados.getDesenvolvedora());

        return ResponseEntity.ok(jogoRepository.save(jogo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        if (!jogoRepository.existsById(id)) {
            throw new IllegalArgumentException("Jogo nao encontrado.");
        }
        jogoRepository.deleteById(id);
        return ResponseEntity.ok("Jogo excluido com sucesso.");
    }
}
