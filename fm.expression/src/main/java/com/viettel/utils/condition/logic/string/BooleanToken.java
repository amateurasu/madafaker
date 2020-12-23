package com.viettel.utils.condition.logic.string;

public enum BooleanToken {
    NO_OPERATOR(""),
    PLUS("+"),
    MINUS("-"),
    GT(">"),
    LT("<"),
    OPEN("("),
    CLOSE(")"),
    ASTERISK("*"),
    TILDE("~"),
    QUOTE("\""),
    STRING(null);

    private final String token;

    BooleanToken(String token) {
        this.token = token;
    }
}
