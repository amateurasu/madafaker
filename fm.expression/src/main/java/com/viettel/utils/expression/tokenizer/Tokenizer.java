/*
 * Copyright 2014 Frank Asseg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.utils.expression.tokenizer;

import com.viettel.utils.expression.Function;
import com.viettel.utils.expression.Operator;

import java.util.Map;
import java.util.Set;

import static com.viettel.utils.expression.tokenizer.Token.*;

public class Tokenizer {

    private final char[] expression;
    private final int expressionLength;
    private final Set<String> variableNames;
    private final Map<String, Function> userFunctions;
    private final Map<String, Operator> userOperators;
    private final boolean implicitMultiplication;

    private int pos = 0;

    private Token lastToken;

    public Tokenizer(String exp, Map<String, Function> fns, Map<String, Operator> ops, Set<String> vars) {
        this(exp, fns, ops, vars, true);
    }

    public Tokenizer(
        String expression,
        final Map<String, Function> userFunctions,
        final Map<String, Operator> userOperators,
        final Set<String> variableNames,
        final boolean implicitMultiplication
    ) {
        this.expression = expression.trim().toCharArray();
        expressionLength = this.expression.length;
        this.userFunctions = userFunctions;
        this.userOperators = userOperators;
        this.variableNames = variableNames;
        this.implicitMultiplication = implicitMultiplication;
    }

    public boolean hasNext() {
        return expression.length > pos;
    }

    public Token nextToken() {
        char ch = expression[pos];
        while (Character.isWhitespace(ch)) {
            ch = expression[++pos];
        }
        if (Character.isDigit(ch) || ch == '.') {
            if (lastToken != null) {
                var type = lastToken.getType();
                if (type == NUMBER) {
                    throw new IllegalArgumentException(
                        "Unable to parse char '" + ch + "' (Code:" + (int) ch + ") at [" + pos + "]");
                } else if (implicitMultiplication && (type != OPERATOR && type != PARENTHESES_OPEN && type != FUNCTION
                    && type != SEPARATOR)) {
                    // insert an implicit multiplication token
                    return lastToken = new OperatorToken(Operator.get('*', 2));
                }
            }
            return parseNumberToken(ch);
        } else if (ch == ',') {
            return parseArgumentSeparatorToken();
        } else if (ch == '(' || ch == '{' || ch == '[') {
            if (lastToken == null || !implicitMultiplication) {
                return parseParentheses(true);
            }
            var type = lastToken.getType();
            if (type == OPERATOR || type == PARENTHESES_OPEN || type == FUNCTION || type == SEPARATOR) {
                return parseParentheses(true);
            }
            // insert an implicit multiplication token
            return lastToken = new OperatorToken(Operator.get('*', 2));
        } else if (ch == ')' || ch == '}' || ch == ']') {
            return parseParentheses(false);
        } else if (Operator.isAllowedOperatorChar(ch)) {
            return parseOperatorToken(ch);
        } else if (Character.isLetter(ch) || ch == '_') {
            // parse the name which can be a setVariable or a function
            if (lastToken == null || !implicitMultiplication) {
                return parseFunctionOrVariable();
            }
            var type = lastToken.getType();
            if (type == OPERATOR || type == PARENTHESES_OPEN || type == FUNCTION || type == SEPARATOR) {
                return parseFunctionOrVariable();
            }
            // insert an implicit multiplication token
            return lastToken = new OperatorToken(Operator.get('*', 2));
        }
        throw new IllegalArgumentException(
            "Unable to parse char '" + ch + "' (Code:" + (int) ch + ") at [" + pos + "]");
    }

    private Token parseArgumentSeparatorToken() {
        pos++;
        return lastToken = new ArgumentSeparatorToken();
    }

    private Token parseParentheses(final boolean open) {
        pos++;
        return lastToken = open ? new OpenParenthesesToken() : new CloseParenthesesToken();
    }

    private Token parseFunctionOrVariable() {
        final int offset = pos;
        int lastValidLen = 1;
        Token lastValidToken = null;
        int len = 1;
        if (isEndOfExpression(offset)) {
            pos++;
        }
        int testPos = offset + len - 1;
        while (!isEndOfExpression(testPos) && isVariableOrFunctionCharacter(expression[testPos])) {
            String name = new String(expression, offset, len);
            if (variableNames != null && variableNames.contains(name)) {
                lastValidLen = len;
                lastValidToken = new VariableToken(name);
            } else {
                final Function f = getFunction(name);
                if (f != null) {
                    lastValidLen = len;
                    lastValidToken = new FunctionToken(f);
                }
            }
            len++;
            testPos = offset + len - 1;
        }
        if (lastValidToken == null) {
            throw new UnknownFunctionOrVariableException(new String(expression), pos, len);
        }
        pos += lastValidLen;
        return lastToken = lastValidToken;
    }

    private Function getFunction(String name) {
        Function f = null;
        if (userFunctions != null) {
            f = userFunctions.get(name);
        }
        return f == null ? Function.get(name) : f;
    }

    private Token parseOperatorToken(char firstChar) {
        final int offset = pos;
        int len = 1;
        final StringBuilder symbol = new StringBuilder();
        Operator lastValid = null;
        symbol.append(firstChar);

        while (!isEndOfExpression(offset + len) && Operator.isAllowedOperatorChar(expression[offset + len])) {
            symbol.append(expression[offset + len++]);
        }

        while (symbol.length() > 0) {
            Operator op = getOperator(symbol.toString());
            if (op == null) {
                symbol.setLength(symbol.length() - 1);
            } else {
                lastValid = op;
                break;
            }
        }

        pos += symbol.length();
        return lastToken = new OperatorToken(lastValid);
    }

    private Operator getOperator(String symbol) {
        Operator op = null;
        if (userOperators != null) {
            op = userOperators.get(symbol);
        }
        if (op == null && symbol.length() == 1) {
            int argc = 2;
            if (lastToken == null) {
                argc = 1;
            } else {
                var lastTokenType = lastToken.getType();
                if (lastTokenType == PARENTHESES_OPEN || lastTokenType == SEPARATOR) {
                    argc = 1;
                } else if (lastTokenType == OPERATOR) {
                    final Operator lastOp = ((OperatorToken) lastToken).getOperator();
                    if (lastOp.getNumOperands() == 2 || (lastOp.getNumOperands() == 1 && !lastOp.isLeftAssociative())) {
                        argc = 1;
                    }
                }
            }
            op = Operator.get(symbol.charAt(0), argc);
        }
        return op;
    }

    private Token parseNumberToken(final char firstChar) {
        final int offset = pos;
        int len = 1;
        pos++;
        if (isEndOfExpression(offset + len)) {
            return lastToken = new NumberToken(Double.parseDouble(String.valueOf(firstChar)));
        }
        while (!isEndOfExpression(offset + len) && isNumeric(expression[offset + len],
            expression[offset + len - 1] == 'e' || expression[offset + len - 1] == 'E')) {
            len++;
            pos++;
        }
        // check if the e is at the end
        if (expression[offset + len - 1] == 'e' || expression[offset + len - 1] == 'E') {
            // since the e is at the end it's not part of the number and a rollback is necessary
            len--;
            pos--;
        }
        var d = Double.parseDouble(String.valueOf(expression, offset, len));
        return lastToken = new NumberToken(d);
    }

    private static boolean isNumeric(char ch, boolean lastCharE) {
        return Character.isDigit(ch) || ch == '.' || ch == 'e' || ch == 'E' || (lastCharE && (ch == '-' || ch == '+'));
    }

    public static boolean isVariableOrFunctionCharacter(int codePoint) {
        return Character.isLetter(codePoint) || Character.isDigit(codePoint) || codePoint == '_' || codePoint == '.';
    }

    private boolean isEndOfExpression(int offset) {
        return offset >= expressionLength;
    }
}
