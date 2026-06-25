package com.rankitup.backend.repository;

import com.rankitup.backend.model.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    Optional<Jogador> findByNickname(String nickname);

    Optional<Jogador> findByEmail(String email);
}
