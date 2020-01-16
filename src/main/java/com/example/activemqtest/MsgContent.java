package com.example.activemqtest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class MsgContent {

    private final String text;
    private final int failures;

    @JsonCreator
    public MsgContent(@JsonProperty("text") String text,
                       @JsonProperty("failures") Integer failCount) {
        this.text = text;
        this.failures = Optional.ofNullable(failCount).orElse(0);
    }

    public String getText() {
        return text;
    }

    public int getFailures() {
        return failures;
    }
}
