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

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class PerformanceTest {

    private static final long BENCH_TIME = 2L;
    private static final String EXPRESSION = "log(x) - y * (sqrt(x^cos(y)))";

    @Benchmark
    public void benchDouble(Blackhole bh) {
        final Expression expression = new ExpressionBuilder(EXPRESSION).variables("x", "y").build();
        Random random = new Random();
        long time = System.currentTimeMillis() + (1000 * BENCH_TIME);
        while (time > System.currentTimeMillis()) {
            expression.setVariable("x", random.nextDouble());
            expression.setVariable("y", random.nextDouble());
            bh.consume(expression.evaluate());
        }
    }

    @Benchmark
    public void benchJavaMath(Blackhole bh) {
        long time = System.currentTimeMillis() + (1000 * BENCH_TIME);
        Random rnd = new Random();
        while (time > System.currentTimeMillis()) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            bh.consume(Math.log(x) - y * (Math.sqrt(Math.pow(x, Math.cos(y)))));
        }
    }

    @Benchmark
    public void benchJavaScript(Blackhole bh) throws Exception {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        Random rnd = new Random();
        if (engine == null) {
            System.err.println("Unable to instantiate javascript engine. skipping naive JS bench.");
            return;
        }

        long time = System.currentTimeMillis() + (1000 * BENCH_TIME);
        while (time > System.currentTimeMillis()) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            bh.consume(engine.eval("Math.log(" + x + ") - " + y + "* (Math.sqrt(" + x + "^Math.cos(" + y + ")))"));
        }
    }
}
