package com.rankitup.backend.repository;

import com.rankitup.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    Optional<Jogo> findByTitulo(String titulo);
}
