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

package com.viettel.utils.expression;

import com.viettel.utils.expression.tokenizer.OperatorToken;
import com.viettel.utils.expression.tokenizer.Token;
import com.viettel.utils.expression.tokenizer.Tokenizer;

import java.util.*;

import static com.viettel.utils.expression.tokenizer.Token.*;

/**
 * Factory class for {@link Expression} instances. This class is the main API entrypoint. Users should create new {@link
 * Expression} instances using this factory class.
 */
public class ExpressionBuilder {

    private final String expression;
    private final Set<String> variableNames;
    private final Map<String, Function> userFunctions;
    private final Map<String, Operator> userOperators;

    private boolean implicitMultiplication = true;

    /**
     * Create a new ExpressionBuilder instance and initialize it with a given expression string.
     *
     * @param expression the expression to be parsed
     */
    public ExpressionBuilder(String expression) {
        if (expression == null || expression.trim().length() == 0) {
            throw new IllegalArgumentException("Expression can not be empty");
        }
        this.expression = expression;
        userOperators = new HashMap<>(4);
        userFunctions = new HashMap<>(4);
        variableNames = new HashSet<>(4);
    }

    /**
     * Add a {@link Function} implementation available for use in the expression
     *
     * @param function the custom {@link Function} implementation that should be available for use in the
     *     expression.
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder function(Function function) {
        var name = function.getName();
        checkFunctionDuplicateVariable(name);
        userFunctions.put(name, function);
        return this;
    }

    public ExpressionBuilder function(String name, int numArgs, IExpression fn) {
        checkFunctionDuplicateVariable(name);
        userFunctions.put(name, new Function(name, numArgs, fn));
        return this;
    }

    public ExpressionBuilder function(String name, IExpression fn) {
        checkFunctionDuplicateVariable(name);
        userFunctions.put(name, new Function(name, fn));
        return this;
    }

    /**
     * Add multiple {@link Function} implementations available for use in the expression
     *
     * @param functions the custom {@link Function} implementations
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(Function... functions) {
        for (Function f : functions) {
            var name = f.getName();
            checkFunctionDuplicateVariable(name);
            userFunctions.put(name, f);
        }
        return this;
    }

    /**
     * Add multiple {@link Function} implementations available for use in the expression
     *
     * @param functions A {@link List} of custom {@link Function} implementations
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder functions(List<Function> functions) {
        for (Function f : functions) {
            var name = f.getName();
            checkFunctionDuplicateVariable(name);
            userFunctions.put(name, f);
        }
        return this;
    }

    /**
     * Declare variable names used in the expression
     *
     * @param variableNames the variables used in the expression
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(Set<String> variableNames) {
        for (String name : variableNames) {
            checkVariableDuplicateFunction(name);
            this.variableNames.add(name);
        }
        return this;
    }

    /**
     * Declare variable names used in the expression
     *
     * @param variableNames the variables used in the expression
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variables(String... variableNames) {
        for (String name : variableNames) {
            checkVariableDuplicateFunction(name);
            this.variableNames.add(name);
        }
        return this;
    }

    /**
     * Declare a variable used in the expression
     *
     * @param variableName the variable used in the expression
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder variable(String variableName) {
        checkVariableDuplicateFunction(variableName);
        variableNames.add(variableName);
        return this;
    }

    public ExpressionBuilder implicitMultiplication(boolean enabled) {
        implicitMultiplication = enabled;
        return this;
    }

    /**
     * Add an {@link Operator} which should be available for use in the expression
     *
     * @param operator the custom {@link Operator} to add
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator operator) {
        checkOperatorSymbol(operator);
        userOperators.put(operator.getSymbol(), operator);
        return this;
    }

    private void checkOperatorSymbol(Operator op) {
        String name = op.getSymbol();
        for (char ch : name.toCharArray()) {
            if (!Operator.isAllowedOperatorChar(ch)) {
                throw new IllegalArgumentException("The operator symbol '" + name + "' is invalid");
            }
        }
    }

    /**
     * Add multiple {@link Operator} implementations which should be available for use in the expression
     *
     * @param operators the set of custom {@link Operator} implementations to add
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(Operator... operators) {
        for (Operator o : operators) {
            operator(o);
        }
        return this;
    }

    /**
     * Add multiple {@link Operator} implementations which should be available for use in the expression
     *
     * @param operators the {@link List} of custom {@link Operator} implementations to add
     *
     * @return the ExpressionBuilder instance
     */
    public ExpressionBuilder operator(List<Operator> operators) {
        for (Operator o : operators) {
            operator(o);
        }
        return this;
    }

    /**
     * Build the {@link Expression} instance using the custom operators and functions set.
     *
     * @return an {@link Expression} instance which can be used to evaluate the result of the expression
     */
    public Expression build() {
        if (expression.length() == 0) {
            throw new IllegalArgumentException("The expression can not be empty");
        }

        /* set the constants' variable names */
        variableNames.add("pi");
        variableNames.add("π");
        variableNames.add("e");
        variableNames.add("φ");

        return new Expression(toRPN(), userFunctions.keySet());
    }

    private void checkVariableDuplicateFunction(String name) {
        if (Function.get(name) != null || userFunctions.containsKey(name)) {
            throw new IllegalArgumentException("A variable can not have the same name as a function [" + name + "]");
        }
    }

    private void checkFunctionDuplicateVariable(String name) {
        if (variableNames.contains(name)) {
            throw new IllegalArgumentException("A function can not have the same name as a variable [" + name + "]");
        }
    }

    /**
     * Convert a Set of tokens from infix to reverse polish notation
     *
     * @return a {@link Token} array containing the result
     */
    public Token[] toRPN() {
        final Stack<Token> stack = new Stack<>();
        final List<Token> output = new ArrayList<>();

        final Tokenizer tokenizer =
            new Tokenizer(expression, userFunctions, userOperators, variableNames, implicitMultiplication);
        while (tokenizer.hasNext()) {
            Token token = tokenizer.nextToken();
            switch (token.getType()) {
                case NUMBER:
                case VARIABLE:
                    output.add(token);
                    break;
                case FUNCTION:
                    stack.add(token);
                    break;
                case SEPARATOR:
                    while (!stack.empty() && stack.peek().getType() != PARENTHESES_OPEN) {
                        output.add(stack.pop());
                    }
                    if (stack.empty() || stack.peek().getType() != PARENTHESES_OPEN) {
                        throw new IllegalArgumentException(
                            "Misplaced function separator ',' or mismatched " + "parentheses");
                    }
                    break;
                case OPERATOR:
                    while (!stack.empty() && stack.peek().getType() == OPERATOR) {
                        var op1 = ((OperatorToken) token).getOperator();
                        var op2 = ((OperatorToken) stack.peek()).getOperator();
                        if (op1.getNumOperands() == 1 && op2.getNumOperands() == 2) {
                            break;
                        } else if ((op1.isLeftAssociative() && op1.getPrecedence() <= op2.getPrecedence()) || (
                            op1.getPrecedence() < op2.getPrecedence())) {
                            output.add(stack.pop());
                        } else {
                            break;
                        }
                    }
                    stack.push(token);
                    break;
                case PARENTHESES_OPEN:
                    stack.push(token);
                    break;
                case PARENTHESES_CLOSE:
                    while (stack.peek().getType() != PARENTHESES_OPEN) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                    if (!stack.isEmpty() && stack.peek().getType() == FUNCTION) {
                        output.add(stack.pop());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Token type encountered. This should not happen");
            }
        }
        while (!stack.empty()) {
            Token t = stack.pop();
            if (t.getType() == PARENTHESES_CLOSE || t.getType() == PARENTHESES_OPEN) {
                throw new IllegalArgumentException("Mismatched parentheses detected. Please check the expression");
            } else {
                output.add(t);
            }
        }
        return output.toArray(new Token[0]);
    }
}
