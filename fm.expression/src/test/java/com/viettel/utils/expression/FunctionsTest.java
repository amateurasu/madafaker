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

import org.junit.jupiter.api.Test;

import static com.viettel.utils.expression.Function.*;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionsTest {
    @Test
    public void testFunctionNameNull() {
        assertThrows(IllegalArgumentException.class, () ->  new Function(null, args -> 0));
    }

    @Test
    public void testFunctionNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Function("", args -> 0));
    }

    @Test
    public void testFunctionNameZeroArgs() {
        Function f = new Function("foo", 0, args -> 0);
        assertEquals(0f, f.apply(), 0f);
    }

    @Test
    public void testFunctionNameNegativeArgs() {
        assertThrows(IllegalArgumentException.class, () -> new Function("foo", -1, args -> 0));
    }

    @Test
    public void testIllegalFunctionName() {
        assertThrows(IllegalArgumentException.class, () -> new Function("1foo", args -> 0));
        assertThrows(IllegalArgumentException.class, () -> new Function("_&oo", args -> 0));
        assertThrows(IllegalArgumentException.class, () -> new Function("o+o", args -> 0));
    }

    @Test
    public void testCheckFunctionNames() {
        assertTrue(isValidFunctionName("log"));
        assertTrue(isValidFunctionName("$log"));
        assertTrue(isValidFunctionName("sin"));
        assertTrue(isValidFunctionName("abz"));
        assertTrue(isValidFunctionName("alongfunctionnamecanhappen"));
        assertTrue(isValidFunctionName("_log"));
        assertTrue(isValidFunctionName("__blah"));
        assertTrue(isValidFunctionName("foox"));
        assertTrue(isValidFunctionName("aZ"));
        assertTrue(isValidFunctionName("Za"));
        assertTrue(isValidFunctionName("ZZaa"));
        assertTrue(isValidFunctionName("_"));
        assertTrue(isValidFunctionName("log2"));
        assertTrue(isValidFunctionName("lo32g2"));
        assertTrue(isValidFunctionName("_o45g2"));

        assertFalse(isValidFunctionName("&"));
        assertFalse(isValidFunctionName("_+log"));
        assertFalse(isValidFunctionName("_k&l"));
        assertFalse(isValidFunctionName("k&l"));
        assertFalse(isValidFunctionName("+log"));
        assertFalse(isValidFunctionName("fo-o"));
        assertFalse(isValidFunctionName("log+"));
        assertFalse(isValidFunctionName("perc%"));
        assertFalse(isValidFunctionName("del$a"));
    }
}
