package com.graydang.app.global.s3.model;

import lombok.Getter;

@Getter
public enum ImagePrefix {

    USER_PROFILE("user-profile/"),
    POLITICAL_TYPE_ANIMAL("political-type/animal/"),
    TEST("test/"),
    DEFAULT("default/");

    ImagePrefix(String prefix) {
        this.value = prefix;
    }

    private final String value;
}
