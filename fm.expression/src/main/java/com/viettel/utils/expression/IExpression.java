package com.viettel.utils.expression;

@FunctionalInterface
public interface IExpression {
    /**
     * Method that does the actual calculation of the function value given the arguments
     *
     * @param args the set of arguments used for calculating the function
     * @return the result of the function evaluation
     */
    double apply(double... args);
}
