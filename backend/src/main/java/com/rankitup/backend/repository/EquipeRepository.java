package com.rankitup.backend.repository;

import com.rankitup.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByTagEquipe(String tagEquipe);
}
