package org.example.sba.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
    @JsonProperty("admin")
    ADMIN, // id = 1
    @JsonProperty("user")
    USER // id = 2
}
