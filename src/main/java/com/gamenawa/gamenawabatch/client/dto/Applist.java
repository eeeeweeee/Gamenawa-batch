package com.gamenawa.gamenawabatch.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Applist {
    private List<App> apps = new ArrayList<>();
}
