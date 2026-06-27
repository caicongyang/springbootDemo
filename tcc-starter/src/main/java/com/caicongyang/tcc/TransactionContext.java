package com.caicongyang.tcc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-local transaction context holding the current transaction's state.
 */
public class TransactionContext {

    private static final ThreadLocal<TransactionContext> CURRENT = new ThreadLocal<>();

    private final String transactionId;
    private TccStatus status;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    // participant key → status
    private final Map<String, TccStatus> participants = new ConcurrentHashMap<>();

    private TransactionContext(String transactionId) {
        this.transactionId = transactionId;
        this.status = TccStatus.TRYING;
    }

    public static TransactionContext begin(String transactionId) {
        TransactionContext ctx = new TransactionContext(transactionId);
        CURRENT.set(ctx);
        return ctx;
    }

    public static TransactionContext current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TccStatus getStatus() {
        return status;
    }

    public void setStatus(TccStatus status) {
        this.status = status;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public void registerParticipant(String participant, TccStatus status) {
        participants.put(participant, status);
    }

    public Map<String, TccStatus> getParticipants() {
        return participants;
    }
}
