package com.rankitup.backend.repository;

import com.rankitup.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesempenhoPartidaRepository extends JpaRepository<DesempenhoPartida, DesempenhoPartidaId> {
    List<DesempenhoPartida> findByPartida_IdPartida(Long idPartida);
}