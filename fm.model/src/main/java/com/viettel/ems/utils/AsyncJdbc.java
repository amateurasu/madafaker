package com.viettel.ems.utils;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@AllArgsConstructor
public class AsyncJdbc {

    @Qualifier("jdbcExecutor")
    private final TaskExecutor jdbcExecutor;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public <T> Future<int[][]> batchInsert(
        String sql, List<T> books, int batchSize, ParameterizedPreparedStatementSetter<T> ppss
    ) {
        return async(() -> jdbc.batchUpdate(sql, books, batchSize, ppss));
    }

    public <T> Future<T> queryOne(String sql, SqlParameterSource params, RowMapper<T> rowMapper) {
        return async(() -> namedJdbc.queryForObject(sql, params, rowMapper));
    }

    public <T> Future<T> queryOne(String sql, RowMapper<T> rowMapper, Object... params) {
        return async(() -> jdbc.queryForObject(sql, params, rowMapper));
    }

    public <T> Future<T> queryOne(String sql, Object[] params, RowMapper<T> rowMapper) {
        return async(() -> jdbc.queryForObject(sql, params, rowMapper));
    }

    public <T> Future<List<T>> query(String sql, SqlParameterSource params, RowMapper<T> rowMapper) {
        return asyncList(() -> namedJdbc.query(sql, params, rowMapper));
    }

    public <T> Future<List<T>> query(String sql, Map<String, Object> params, RowMapper<T> rowMapper) {
        return asyncList(() -> namedJdbc.query(sql, params, rowMapper));
    }

    public <T> Future<List<T>> query(String sql, RowMapper<T> rowMapper, Object... params) {
        return asyncList(() -> jdbc.query(sql, params, rowMapper));
    }

    public <T> Future<List<T>> query(String sql, Object[] params, RowMapper<T> rowMapper) {
        return asyncList(() -> jdbc.query(sql, params, rowMapper));
    }

    public <K, V> Future<Map<K, V>> queryMap(String sql, Object[] params, Map<K, V> map, Mapper<K, V> mapper) {
        return async(() -> jdbc.query(sql, params, rs -> {
            while (rs.next()) {
                mapper.map(map, rs);
            }
            return map;
        }));
    }

    public <K, V> Future<Map<K, V>> queryMap(String sql, Object[] params, Mapper<K, V> mapper) {
        Map<K, V> map = new HashMap<>();
        return queryMap(sql, params, map, mapper);
    }

    public Future<Integer> update(String sql, Map<String, ?> params) {
        return async(() -> namedJdbc.update(sql, params));
    }

    public Future<Integer> update(String sql, SqlParameterSource params) {
        return async(() -> namedJdbc.update(sql, params));
    }

    public Future<Integer> update(String sql, Object... params) {
        return async(() -> jdbc.update(sql, params));
    }

    public <T> Future<T> async(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        jdbcExecutor.execute(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception e) {
                future.complete(null);
            }
        });
        return future;
    }

    public <T> Future<List<T>> asyncList(Callable<List<T>> callable) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        jdbcExecutor.execute(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception e) {
                future.complete(null);
            }
        });
        return future;
    }

    public interface Mapper<K, V> {
        void map(Map<K, V> map, ResultSet rs);
    }
}
