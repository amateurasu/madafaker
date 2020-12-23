package com.viettel.utils.condition.logic.string;

import lombok.Data;

@Data
public class BooleanTokenizer {
    private final String string;

    public void tokenize() {

    }

    public static void main(String[] args) {
        System.out.println("AL123456".compareTo("AL12345"));
    }
}
