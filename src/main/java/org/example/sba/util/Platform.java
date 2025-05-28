package org.example.sba.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Platform {
    @JsonProperty("web")
    WEB,
    @JsonProperty("ios")
    IOS,
    @JsonProperty("android")
    ANDROID;
}