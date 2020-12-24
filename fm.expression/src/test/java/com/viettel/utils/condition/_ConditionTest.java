package com.viettel.utils.condition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.utils.condition.reflection.Reflect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
public class _ConditionTest {
    private Item item;
    private String json;

    @BeforeEach
    public void init() throws IOException {
        item = Item.builder()
            .id(1)
            .name("Horizon")
            .qty(20)
            .available(true)
            .instant(LocalDateTime.now())
            .available(true)
            .description("steam sale of winter")
            .build();
        json = new String(_ConditionTest.class.getResourceAsStream("test.json").readAllBytes());
        log.info("Done reading JSON");
        Reflect.load(Item.class);
        log.info("Done loading reflection information");
    }

    @Test
    @Disabled
    public void benchmark() throws RunnerException {
        Options opt = new OptionsBuilder().include(getClass().getSimpleName()).forks(1).build();
        new Runner(opt).run();
    }

    @Test
    @Benchmark
    public void conditionBenchmark(Blackhole bh) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        bh.consume(mapper.readValue(json, new TypeReference<Condition>() { }));
    }

    @Test
    public void conditionEvaluationTest() throws Exception {
        var mapper = new ObjectMapper();
        var condition = mapper.readValue(json, new TypeReference<Condition>() { });
        var evaluate = condition.evaluate(item);
        log.info("evaluate {}", evaluate);
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
}

// {
//     "$and": [
//         {
//             "$or": [
//                 {"id": {"$in": [1, 1.1, 2, 3, 4]}}
//             ]
//         }, {
//             "$and": [
//                 {"#{time(trigger_time)}": {"$ge": "09:45", "$le": "23:45"}},
//                 {"#{date(trigger_time)}": {"$ge": "2020-08-19", "$le": "2020-09-02"}},
//                 {"time": {"$ge": 123456789}},
//                 {"available": true},
//                 {"id": {"$nin": [1.1, 2, 3, 4]}}
//             ]
//         }, {
//             "#{name,description}": {"$match": "summer sale"}
//         }
//     ]
// }
