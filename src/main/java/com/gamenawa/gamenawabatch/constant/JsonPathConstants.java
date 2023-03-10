package com.gamenawa.gamenawabatch.constant;

public final class JsonPathConstants {
    public static final String JSONPATH_METACRITIC_SCORE = "$..metacritic.score";
    public static final String JSONPATH_DEVELOPERS = "$..publishers";
    public static final String JSONPATH_PUBLISHERS = "$..developers";
    public static final String JSONPATH_IMAGE_URL = "$..header_image";
    public static final String JSONPATH_RELEASEDT = "$..release_date.date";
    public static final String JSONPATH_STEAM_SCORE = "$.query_summary.review_score_desc";
    public static final String JSONPATH_TYPE = "$..type";
    public static String getJsonpathMetacriticSuccess(int appId) {
        return "$." + appId + ".success";
    }
}
