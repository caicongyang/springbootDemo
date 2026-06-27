package com.caicongyang.seq;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Database-backed sequence generator using a MySQL sequence table.
 *
 * <p>Expected table:
 * <pre>
 * CREATE TABLE seq_table (
 *   biz_key VARCHAR(64) NOT NULL PRIMARY KEY,
 *   current_id BIGINT NOT NULL DEFAULT 0
 * );
 * </pre>
 */
public class DatabaseSeqGenerator implements SeqGenerator {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSeqGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long nextId(String bizKey) {
        // UPSERT: insert if not exists, then increment and return
        jdbcTemplate.update(
                "INSERT INTO seq_table (biz_key, current_id) VALUES (?, 0) " +
                "ON DUPLICATE KEY UPDATE biz_key = biz_key",
                bizKey);
        jdbcTemplate.update(
                "UPDATE seq_table SET current_id = LAST_INSERT_ID(current_id + 1) " +
                "WHERE biz_key = ?",
                bizKey);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id != null ? id : 0L;
    }
}
