package com.gamenawa.gamenawabatch.client;

import com.gamenawa.gamenawabatch.client.dto.ApplistContainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Steam API를 호출하는 Feign Client입니다.
 */
@FeignClient(
        name = "SteamAppClient",
        url = "http://api.steampowered.com/ISteamApps/GetAppList/v0002/?format=json")
public interface SteamAppClient {
    @GetMapping
    ApplistContainer getAllGames();
}

