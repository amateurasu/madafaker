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

import lombok.Data;

import static java.lang.Math.*;

/** A class representing a Function which can be used in an expression */
@Data
public class Function {
    //region BUILT-IN FUNCTIONS
    private static final Function FN_SIN = new Function("sin", args -> sin(args[0]));
    private static final Function FN_COS = new Function("cos", args -> cos(args[0]));
    private static final Function FN_TAN = new Function("tan", args -> tan(args[0]));
    private static final Function FN_COT = new Function("cot", args -> {
        double tan = tan(args[0]);
        if (tan == 0d) {
            throw new ArithmeticException("Division by zero in cotangent!");
        }
        return 1d / tan;
    });

    private static final Function FN_CSC = new Function("csc", args -> {
        double sin = sin(args[0]);
        if (sin == 0d) throw new ArithmeticException("Division by zero in cosecant!");
        return 1d / sin;
    });
    private static final Function FN_SEC = new Function("sec", args -> {
        double cos = cos(args[0]);
        if (cos == 0d) throw new ArithmeticException("Division by zero in secant!");
        return 1d / cos;
    });

    private static final Function FN_SINH = new Function("sinh", args -> sinh(args[0]));
    private static final Function FN_COSH = new Function("cosh", args -> cosh(args[0]));
    private static final Function FN_TANH = new Function("tanh", args -> tanh(args[0]));

    private static final Function FN_CSCH = new Function("csch", args -> {
        //this would throw an ArithmeticException later as sinh(0) = 0
        if (args[0] == 0d) return 0;
        return 1d / sinh(args[0]);
    });
    private static final Function FN_SECH = new Function("sech", args -> 1d / cosh(args[0]));
    private static final Function FN_COTH = new Function("coth", args -> cosh(args[0]) / sinh(args[0]));

    private static final Function FN_ASIN = new Function("asin", args -> asin(args[0]));
    private static final Function FN_ACOS = new Function("acos", args -> acos(args[0]));
    private static final Function FN_ATAN = new Function("atan", args -> atan(args[0]));

    private static final Function FN_ABS = new Function("abs", args -> abs(args[0]));
    private static final Function FN_SGN = new Function("signum", args -> {
        if (args[0] > 0) return 1;
        if (args[0] < 0) return -1;
        return 0;
    });
    private static final Function FN_CEIL = new Function("ceil", args -> ceil(args[0]));
    private static final Function FN_FLOOR = new Function("floor", args -> floor(args[0]));

    private static final Function FN_POW = new Function("pow", 2, args -> pow(args[0], args[1]));
    private static final Function FN_SQRT = new Function("sqrt", args -> sqrt(args[0]));
    private static final Function FN_CBRT = new Function("cbrt", args -> cbrt(args[0]));

    private static final Function FN_EXP = new Function("exp", args -> exp(args[0]));
    private static final Function FN_EXPM1 = new Function("expm1", args -> expm1(args[0]));
    private static final Function FN_LOG10 = new Function("log10", args -> log10(args[0]));
    private static final Function FN_LOG2 = new Function("log2", args -> log(args[0]) / log(2d));
    private static final Function FN_LOG = new Function("log", args -> log(args[0]));
    private static final Function FN_LOG1P = new Function("log1p", args -> log1p(args[0]));
    private static final Function FN_LOGB = new Function("logb", 2, args -> log(args[1]) / log(args[0]));

    private static final Function FN_TO_RADIAN = new Function("toradian", args -> toRadians(args[0]));
    private static final Function FN_TO_DEGREE = new Function("todegree", args -> toDegrees(args[0]));
    //endregion BUILT-IN FUNCTIONS

    private final String name;
    protected final int numArguments;
    private final IExpression fn;

    /**
     * Create a new Function with a given name and number of arguments
     *
     * @param name the name of the Function
     * @param numArguments the number of arguments the function takes
     */
    public Function(String name, int numArguments, IExpression fn) {
        if (numArguments < 0) {
            throw new IllegalArgumentException(
                "The number of function arguments can not be less than 0 for '" + name + "'");
        }
        if (!isValidFunctionName(name)) {
            throw new IllegalArgumentException("The function name '" + name + "' is invalid");
        }
        this.name = name;
        this.numArguments = numArguments;
        this.fn = fn;
    }

    /**
     * Create a new Function with a given name that takes a single argument
     *
     * @param name the name of the Function
     */
    public Function(String name, IExpression fn) {
        this(name, 1, fn);
    }

    /**
     * Get the builtin function for a given name
     *
     * @param name te name of the function
     *
     * @return a Function instance
     */
    public static Function get(final String name) {
        switch (name) {
            case "sin": return FN_SIN;
            case "cos": return FN_COS;
            case "tan": return FN_TAN;
            case "cot": return FN_COT;

            case "asin": return FN_ASIN;
            case "acos": return FN_ACOS;
            case "atan": return FN_ATAN;

            case "sinh": return FN_SINH;
            case "cosh": return FN_COSH;
            case "tanh": return FN_TANH;
            case "coth": return FN_COTH;

            case "csc": return FN_CSC;
            case "sec": return FN_SEC;
            case "csch": return FN_CSCH;
            case "sech": return FN_SECH;

            case "abs": return FN_ABS;
            case "signum": return FN_SGN;
            case "ceil": return FN_CEIL;
            case "floor": return FN_FLOOR;

            case "log": return FN_LOG;
            case "log10": return FN_LOG10;
            case "log2": return FN_LOG2;
            case "log1p": return FN_LOG1P;
            case "logb": return FN_LOGB;

            case "pow": return FN_POW;
            case "sqrt": return FN_SQRT;
            case "cbrt": return FN_CBRT;

            case "exp": return FN_EXP;
            case "expm1": return FN_EXPM1;

            case "toradian": return FN_TO_RADIAN;
            case "todegree": return FN_TO_DEGREE;
            default: return null;
        }
    }

    public static boolean isValidFunctionName(final String name) {
        if (name == null) return false;

        final int size = name.length();
        if (size == 0) return false;

        final char ch = name.charAt(0);
        if (Character.isLetter(ch) || ch == '_' || ch == '$') {
            for (int i = 1; i < size; i++) {
                final char c = name.charAt(i);
                if (Character.isLetter(c) || c == '_' || Character.isDigit(c)) continue;
                return false;
            }
            return true;
        }

        return false;
    }

    public double apply(double... args) {
        return fn.apply(args);
    }
}
