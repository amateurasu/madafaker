package com.viettel.utils.condition.logic.string;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Data
@Slf4j
public class WordIterable implements Iterable<String> {

    private final String str;

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private final int length = str.length() - 1;
            private int index = 0;

            private int nextNonWhiteSpace(int start) {
                for (var i = start; i < length; i++) {
                    if (!isWhitespace(str.charAt(i))) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public boolean hasNext() {
                return (index = nextNonWhiteSpace(index)) < length && index >= 0;
            }

            @Override
            public String next()


            {
                for (var i = index; i <= length; i++) {
                    char c = str.charAt(i);
                    if (isWhitespace(c)) {
                        return str.substring(index, index = i);
                    }
                }

                return str.substring(index, index = length + 1);
            }

            private boolean isWhitespace(char c) {
                return c == ' ' || c == '\t' || c == '\n';
            }
        };
    }
}
