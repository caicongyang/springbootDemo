package com.caicongyang.seq;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SeqGeneratorTest {

    private final SnowflakeSeqGenerator generator = new SnowflakeSeqGenerator(1, 1);

    @Test
    void shouldGeneratePositiveId() {
        long id = generator.nextId("order");
        assertTrue(id > 0, "Generated ID should be positive, got: " + id);
    }

    @Test
    void shouldGenerateUniqueIds() {
        Set<Long> ids = new HashSet<>();
        int count = 10_000;
        for (int i = 0; i < count; i++) {
            long id = generator.nextId("order");
            assertTrue(ids.add(id), "Duplicate ID found: " + id);
        }
        assertEquals(count, ids.size());
    }

    @Test
    void shouldGenerateIncreasingIds() {
        long prev = generator.nextId("order");
        for (int i = 0; i < 1_000; i++) {
            long next = generator.nextId("order");
            assertTrue(next > prev, "ID " + next + " should be greater than " + prev);
            prev = next;
        }
    }

    @Test
    void shouldRejectInvalidWorkerId() {
        assertThrows(IllegalArgumentException.class,
                () -> new SnowflakeSeqGenerator(-1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new SnowflakeSeqGenerator(32, 1));
    }

    @Test
    void shouldRejectInvalidDatacenterId() {
        assertThrows(IllegalArgumentException.class,
                () -> new SnowflakeSeqGenerator(1, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new SnowflakeSeqGenerator(1, 32));
    }

    @Test
    void shouldWorkWithDifferentBizKeys() {
        long a = generator.nextId("order");
        long b = generator.nextId("user");
        assertTrue(a > 0);
        assertTrue(b > 0);
        assertNotEquals(a, b);
    }
}
