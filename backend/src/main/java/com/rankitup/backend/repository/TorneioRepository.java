package com.rankitup.backend.repository;

import com.rankitup.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TorneioRepository extends JpaRepository<Torneio, Long> {
    Optional<Torneio> findByNome(String nome);
}
