package com.gamenawa.gamenawabatch.repository;

import com.gamenawa.gamenawabatch.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
     Optional<Game> findByAppId(int appId);
}
