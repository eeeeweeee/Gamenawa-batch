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
import net.minidev.json.JSONArray;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.gamenawa.gamenawabatch.constant.JsonPathConstants.*;

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
    public Step getNewGameIdsAndNames() {
        return stepBuilderFactory
                .get("GetGameIdsAndNames")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Start get game ids and names...");
                    List<App> games = steamAppClient.getAllGames().getApplist().getApps();
                    for (int i = 0; i < games.size(); i++) {
                        App app = games.get(i);
                        if (gameNeedsUpdate(app)) {
                            Game game = new Game(app.getAppid(), app.getName());

                            // Set game info
                            try {
                                setSteamScore(app, game);
                                setBasicInfoAndMetacriticScore(app, game);
                            } catch (IllegalStateException e) {
                                log.info(e.getMessage());
                                continue;
                            } catch (FeignException.TooManyRequests e) {
                                log.info("Too Many Request.. Try tomorrow");
                                break;
                            }

                            // Save Entity
                            try {
                                game = gameRepository.save(game);
                                log.info("Successfully Saved! {}", game);
                            } catch (Exception e) {
                            }
                        }
                    }
                    log.info("Finished get game ids and names!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    private void setSteamScore(App app, Game game) {
        String resultJson = steamStoreClient.getSteamScore(app.getAppid());
        String steamScore = JsonPath.parse(resultJson).read(JSONPATH_STEAM_SCORE);
        game.setSteamScore(steamScore);
    }

    private void setBasicInfoAndMetacriticScore(App app, Game game) {
        String metacriticScore = "No Score";
        String gameDetailJson = steamStoreClient.getGameDetail(app.getAppid());

        // check app type
        String type = JsonPath.parse(gameDetailJson).read(JSONPATH_TYPE).toString();
        if (!type.contains("game")) {
            throw new IllegalStateException("It is not a game : " + type);
        }

        // Set game basic info
        JSONArray imageUrls = JsonPath.parse(gameDetailJson).read(JSONPATH_IMAGE_URL);
        if (imageUrls.size() > 0) {
            game.setImageUrl(imageUrls.get(0).toString());
        }
        game.setPublishers(JsonPath.parse(gameDetailJson).read(JSONPATH_PUBLISHERS).toString());
        game.setDevelopers(JsonPath.parse(gameDetailJson).read(JSONPATH_DEVELOPERS).toString());
        JSONArray jsonArray = JsonPath.parse(gameDetailJson).read(JSONPATH_RELEASEDT);
        if (jsonArray.size() > 0) {
            game.setReleaseDt(jsonArray.get(0).toString());
        }

        // Set metacritic score
        try {
            if (JsonPath.parse(gameDetailJson).read(getJsonpathMetacriticSuccess(app.getAppid())).toString().compareToIgnoreCase("true") == 0) {
                metacriticScore = JsonPath.parse(gameDetailJson).read(JSONPATH_METACRITIC_SCORE);
            }
        } catch (FeignException.TooManyRequests e) {
            throw e;
        } catch (Exception e) {
            metacriticScore = "No Score";
        }
        game.setMetacriticScore(metacriticScore);
    }

    private boolean gameNeedsUpdate(App app) {
        if (app == null || app.getName().isEmpty()) return false;
        Optional<Game> optionalGame = gameRepository.findByAppId(app.getAppid());
        if (optionalGame.isEmpty()) return true;

        Game game = optionalGame.get();
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return game.getModifiedDt().isBefore(oneMonthAgo); // 마지막 업데이트가 한달 전이라면 정보 갱신
    }
}
