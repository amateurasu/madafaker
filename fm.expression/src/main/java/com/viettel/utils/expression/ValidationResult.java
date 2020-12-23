package com.viettel.utils.expression;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** Contains the validation result for a given {@link Expression} */
@Data
@AllArgsConstructor
public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    /** A static class representing a successful validation result */
    public static final ValidationResult SUCCESS = new ValidationResult(true, null);
}
