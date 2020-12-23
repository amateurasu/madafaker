package com.viettel.utils.expression.tokenizer;

import lombok.*;

/**
 * This exception is being thrown whenever {@link Tokenizer} finds unknown function or variable.
 *
 * @author Bartosz Firyn (sarxos)
 */
@Data
@EqualsAndHashCode(callSuper = true)
class UnknownFunctionOrVariableException extends IllegalArgumentException {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private final String message;
    private final String expression;
    private final String token;
    private final int position;

    public UnknownFunctionOrVariableException(String expression, int position, int length) {
        this.expression = expression;
        token = token(expression, position, length);
        this.position = position;
        message = "Unknown function or variable '" + token + "' at pos " + position + " in expression '" + expression + "'";
    }

    private static String token(String expression, int position, int length) {

        int len = expression.length();
        int end = position + length - 1;

        if (len < end) {
            end = len;
        }

        return expression.substring(position, end);
    }
}
