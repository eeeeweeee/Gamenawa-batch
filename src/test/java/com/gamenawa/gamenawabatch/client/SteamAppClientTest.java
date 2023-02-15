package com.gamenawa.gamenawabatch.client;

import com.gamenawa.gamenawabatch.client.dto.App;
import com.gamenawa.gamenawabatch.client.dto.ApplistContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SteamAppClientTest {
    @Autowired
    public SteamAppClient subject;

    @Test
    public void getAllGames() {
        ApplistContainer container = subject.getAllGames();
        for (int i = 0; i < 20; ++i) {
            App game = container.getApplist().getApps().get(i);
            System.out.println("game.getAppid() = " + game.getAppid());
            System.out.println("game.getName() = " + game.getName());
        }
    }
}