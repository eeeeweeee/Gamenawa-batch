package com.gamenawa.gamenawabatch.domain;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private int appid;

    @NotNull
    private String name;

    private String steamScore;

    private String metacriticScore;

    @Builder
    public Game(int appid, String name, String steamScore, String metacriticScore) {
        this.appid = appid;
        this.name = name;
        this.steamScore = steamScore;
        this.metacriticScore = metacriticScore;
    }
}
