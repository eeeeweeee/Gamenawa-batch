package com.gamenawa.gamenawabatch.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
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

    private String developers;

    private String publishers;

    private String imageUrl;

    private String releaseDt;

    public Game(int appId, String name) {
        this.appId = appId;
        this.name = name;
        this.opencriticMedianScore = "No Score";
        this.opencriticTopcriticScore = "No Score";
    }
}
