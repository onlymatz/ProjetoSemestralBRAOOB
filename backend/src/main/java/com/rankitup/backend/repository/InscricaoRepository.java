package com.rankitup.backend.repository;

import com.rankitup.backend.model.Inscricao;
import com.rankitup.backend.model.enums.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    boolean existsByTorneio_IdTorneioAndJogador_IdUsuario(Long idTorneio, Long idUsuario);

    // Busca inscrições por torneio e status — útil para listar só os aprovados
    List<Inscricao> findByTorneio_IdTorneioAndStatus(Long idTorneio, StatusInscricao status);

    // Busca inscrições pendentes de um torneio — para o admin aprovar/rejeitar
    List<Inscricao> findByTorneio_IdTorneioAndStatusOrderByIdInscricaoAsc(Long idTorneio, StatusInscricao status);
}
