package com.rankitup.backend.controller;

import com.rankitup.backend.model.Equipe;
import com.rankitup.backend.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private EquipeRepository equipeRepository;

    @GetMapping
    public List<Equipe> listarTodas() {
        return equipeRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Equipe> cadastrar(@RequestBody Equipe novaEquipe) {
        Equipe equipeSalva = equipeRepository.save(novaEquipe);
        return ResponseEntity.ok(equipeSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipe> atualizar(@PathVariable Long id, @RequestBody Equipe dadosAtualizados) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipe nao encontrada."));

        if (dadosAtualizados.getNomeEquipe() != null && !dadosAtualizados.getNomeEquipe().isBlank()) {
            equipe.setNomeEquipe(dadosAtualizados.getNomeEquipe());
        }
        if (dadosAtualizados.getTagEquipe() != null && !dadosAtualizados.getTagEquipe().isBlank()) {
            equipe.setTagEquipe(dadosAtualizados.getTagEquipe());
        }
        equipe.setPaisOrigem(dadosAtualizados.getPaisOrigem());

        return ResponseEntity.ok(equipeRepository.save(equipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new IllegalArgumentException("Equipe nao encontrada.");
        }
        equipeRepository.deleteById(id);
        return ResponseEntity.ok("Equipe excluida com sucesso.");
    }
}
