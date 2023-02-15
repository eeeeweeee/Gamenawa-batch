package com.gamenawa.gamenawabatch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "SteamClient",
        url = "https://store.steampowered.com/")
public interface SteamStoreClient {
    @GetMapping("api/appdetails?appids={appid}")
    String getMetacriticScore(@PathVariable("appid") int appid);

    @GetMapping("appreviews/{appid}?json=1")
    String getSteamScore(@PathVariable("appid") int appid);
}
// https://store.steampowered.com/api/appdetails?appids=218620
// https://store.steampowered.com/appreviews/218620?json=1