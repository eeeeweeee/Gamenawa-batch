package com.gamenawa.gamenawabatch.client;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SteamStoreClientTest {

    @Autowired
    private SteamStoreClient subject;

    @Test
    void testSteamStoreClient() {
        String jsonPathToMetaScore = "*.metacritic.score";
        String resultJson = subject.getGameDetail(218620);
        String score = JsonPath.parse(resultJson).read(jsonPathToMetaScore);
        System.out.println("score = " + score);
    }

    @Test
    void testGetSteamScore() {
        String jsonPathToMetaScore = "$.query_summary.review_score_desc";
        String resultJson = subject.getSteamScore(218620);
        String score = JsonPath.parse(resultJson).read(jsonPathToMetaScore);
        System.out.println("score = " + score);
    }
}
