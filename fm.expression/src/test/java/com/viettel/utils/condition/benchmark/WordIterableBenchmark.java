package com.viettel.utils.condition.benchmark;

import com.viettel.utils.condition.logic.string.WordIterable;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
public class WordIterableBenchmark {

    private String data;

    @Setup
    public void setup() {
        data = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod\n"
            + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\n"
            + "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo\n"
            + "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse\n"
            + "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non\n"
            + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

    @Test
    public void main() {
        Options opt = new OptionsBuilder().include(WordIterableBenchmark.class.getSimpleName()).forks(1).build();

        try {
            new Runner(opt).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public void benchmarkIterable(Blackhole blackhole) {
        var strings = new WordIterable(data);
        for (String s : strings) {
            blackhole.consume(s);
        }
    }

    @Benchmark
    public void benchmarkStringTokenizer(Blackhole blackhole) {
        var st = new StringTokenizer(data);
        while (st.hasMoreTokens()) {
            blackhole.consume(st.nextToken());
        }
    }

    @Benchmark
    public void benchmarkSplit(Blackhole blackhole) {
        var split = data.split("\\s+");
        for (int i = 0, length = split.length; i < length; i++) {
            blackhole.consume(split[i]);
        }
    }
}
