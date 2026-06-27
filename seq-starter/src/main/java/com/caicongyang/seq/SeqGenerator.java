package com.caicongyang.seq;

/**
 * Sequence generator interface.
 */
public interface SeqGenerator {

    /**
     * Generate the next unique sequence ID for the given business key.
     *
     * @param bizKey business key (e.g. order, user)
     * @return next ID
     */
    long nextId(String bizKey);
}
