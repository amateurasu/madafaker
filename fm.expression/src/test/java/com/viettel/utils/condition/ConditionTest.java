package com.viettel.utils.condition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.logic.StringCondition;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
public class ConditionTest {
    private Item item;
    private String json;

    @BeforeEach
    public void init() throws IOException {
        item = Item.builder()
            .id(1)
            .name("Horizon")
            .qty(20)
            .available(true)
            .instant(LocalDateTime.of(2020, 8, 31, 7, 7, 7))
            .available(true)
            .description("steam sale of winter")
            .build();
        json = new String(ConditionTest.class.getResourceAsStream("update.json").readAllBytes());
        log.info("Done reading JSON");
        Reflect.load(Item.class);
        log.info("Done loading reflection information");
    }

    @Test
    public void checkInput() {
        log.info("Item: {}", item);
        assertNotNull(item.getDate());
        assertNotNull(item.getTime());
    }

    @Test
    // @Disabled
    public void benchmark() throws RunnerException {
        var opt = new OptionsBuilder().include(getClass().getSimpleName()).forks(1).build();
        new Runner(opt).run();
    }

    @Test
    @Benchmark
    public void conditionBenchmark(Blackhole bh) throws IOException {
        var mapper = new ObjectMapper();
        bh.consume(mapper.readValue(json, new TypeReference<Condition>() { }));
    }

    @Test
    public void conditionEvaluationTest() {
        try {
            var mapper = new ObjectMapper();
            var condition = mapper.readValue(json, new TypeReference<Condition>() { });
            log.info("parsed: {}", condition);
            log.info("evaluate {}", condition.evaluate(item));
            log.info("validate {}", condition.validate(Item.class));
            log.info("-----------------------------");
            var query = condition.buildQuery(Item.class);
            log.info("query WHERE \n{}", query.getSql());
            log.info("params: {}", query.getParams());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void conditionQueryTest() throws Exception {
        var mapper = new ObjectMapper();
        var condition = mapper.readValue(json, new TypeReference<Condition>() { });
        log.info("condition {}", condition);

        var query = condition.buildQuery(new Query<Item>(Item.class));
        log.info("SQL '{}'", query.getSql());
        log.info("Params '{}'", query.getParams());
    }

    @Test
    public void testMatch() {
        try {
            var stringCondition = StringCondition.of("description", "alike", "summer sale");
            var evaluate = stringCondition.evaluate(item);
            assertTrue(evaluate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
