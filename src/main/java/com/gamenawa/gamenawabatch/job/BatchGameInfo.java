package com.gamenawa.gamenawabatch.job;

import com.gamenawa.gamenawabatch.client.SteamAppClient;
import com.gamenawa.gamenawabatch.client.SteamStoreClient;
import com.gamenawa.gamenawabatch.client.dto.App;
import com.gamenawa.gamenawabatch.domain.Game;
import com.gamenawa.gamenawabatch.repository.GameRepository;
import com.jayway.jsonpath.JsonPath;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchGameInfo {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SteamAppClient steamAppClient;
    private final SteamStoreClient steamStoreClient;
    private final GameRepository gameRepository;

    @Bean
    public Job batchGameInfoJob() {
        return jobBuilderFactory
                .get("BatchGameInfoJob")
                .start(getNewGameIdsAndNames())
                .build();
    }

    @Bean
    public Step cleanAllGameIdsAndNames() {
        return stepBuilderFactory
                .get("CleanGameIdsAndNames")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Start clean game ids and names...");
                    gameRepository.deleteAll();
                    log.info("Finished clean game ids and names!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step getNewGameIdsAndNames() {
        return stepBuilderFactory
                .get("GetGameIdsAndNames")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Start get game ids and names...");
                    List<App> games = steamAppClient.getAllGames().getApplist().getApps();
                    for (int i = 0; i < games.size(); i++) {
                        App game = games.get(i);
                        if (!game.getName().isEmpty() && gameRepository.findByAppid(game.getAppid()).isEmpty()) {
                            Game gameEntity = Game.builder()
                                    .appid(game.getAppid())
                                    .name(game.getName())
                                    .build();

                            if (gameRepository.existsByAppid(gameEntity.getAppid())) continue;

                            // Get steam score
                            String resultJson = steamStoreClient.getSteamScore(game.getAppid());
                            String jsonPathToSteamScore = "$.query_summary.review_score_desc";
                            String steamScore = JsonPath.parse(resultJson).read(jsonPathToSteamScore);

                            gameEntity.setSteamScore(steamScore);

                            // Get metacritic score
                            String jsonPathToMetacriticScore = "$..metacritic.score";
                            String metacriticScoreSuccessJsonPath = "$." + game.getAppid() + ".success";

                            String metacriticScore = "No Score";
                            try {
                                resultJson = steamStoreClient.getMetacriticScore(game.getAppid());
                                if (JsonPath.parse(resultJson).read(metacriticScoreSuccessJsonPath).toString().compareToIgnoreCase("true") == 0) {
                                    metacriticScore = JsonPath.parse(resultJson).read(jsonPathToMetacriticScore);
                                }
                            } catch (FeignException.TooManyRequests e) {
                                log.info("Too Many Request.. Try tomorrow");
                                break;
                            } catch (Exception e) {
                                metacriticScore = "No Score";
                            }
                            gameEntity.setMetacriticScore(metacriticScore);

                            // Save Entity
                            try {
                                gameEntity = gameRepository.save(gameEntity);
                                log.info("Successfully Saved! {}", gameEntity);
                            } catch (Exception e) {}
                        }
                    }
                    log.info("Finished get game ids and names!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

//    @Bean
//    public Step getGameDetail() {
//        return stepBuilderFactory
//                .get("GetGameDetail")
//                .tasklet((contribution, chunkContext) -> {
//                    log.info("Start get game detail...");
//                    List<SimpleGame> simpleGames = simpleGameRepository.findAll();
//                    simpleGames.forEach(simpleGame -> {
//                        int appid = simpleGame.getAppid();
//
//                    });
//                })
//                .build();
//    }
}
