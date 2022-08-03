package com.identity.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class IdentityEvent implements IIdentityEvent, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("type")
    private Resource type;
    @JsonProperty("path")
    private String path;
    @JsonProperty("action")
    private Action action;
    @JsonProperty("changeLog")
    private ChangeLog changeLog;
    @JsonProperty("data")
    private Map<String, String> data = new HashMap<>();

    public enum Resource{
        USER("user"),
        ROLE("role"),
        GROUP("group");

        private final String type;
        Resource(String type) { this.type = type; }
    }

    public enum Action {
        CREATE("create"),
        UPDATE("update"),
        DELETE("delete");

        private final String type;
        Action(String type) { this.type = type; }
    }

    @JsonInclude(Include.NON_NULL)
    public @Data static class ChangeLog {
        @JsonProperty("originUserId")
        private UUID originUserId;
        @JsonProperty("originDateTime")
        private Date originDateTime;
        @JsonProperty("originIpAddress")
        private String originIpAddress;
        @JsonProperty("originSourceChannel")
        private String originSourceChannel = "Identity Manager";
        @JsonProperty("originSourceRealm")
        private String originSourceRealm;
    }
}