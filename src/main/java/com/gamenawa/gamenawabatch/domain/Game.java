package com.gamenawa.gamenawabatch.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Game extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private int appId;

    @NotNull
    private String name;

    private String steamScore;

    private String metacriticScore;

    private String opencriticMedianScore;

    private String opencriticTopcriticScore;

    private String developer;

    private String publisher;

    private String imageUrl;

    private LocalDateTime releaseDt;

    @Builder
    public Game(int appId, String name, String steamScore, String metacriticScore, String developer, String publisher, String imageUrl, LocalDateTime releaseDt) {
        this.appId = appId;
        this.name = name;
        this.steamScore = steamScore;
        this.metacriticScore = metacriticScore;
        this.opencriticMedianScore = "No Score";
        this.opencriticTopcriticScore = "No Score";
        this.developer = developer;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.releaseDt = releaseDt;
    }
}
