package com.caicongyang.tcc;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Coordinates TCC transaction lifecycle: begin, confirm, cancel.
 */
public class TccCoordinator {

    private static final Logger log = LoggerFactory.getLogger(TccCoordinator.class);

    /**
     * Begin a new TCC transaction and bind it to the current thread.
     */
    public TransactionContext begin() {
        String transactionId = UUID.randomUUID().toString();
        TransactionContext ctx = TransactionContext.begin(transactionId);
        log.debug("TCC transaction [{}] started", transactionId);
        return ctx;
    }

    /**
     * Mark the current transaction as confirmed.
     */
    public void confirm(TransactionContext ctx) {
        if (ctx == null) {
            return;
        }
        if (ctx.getStatus() == TccStatus.CANCELLED) {
            log.warn("TCC transaction [{}] already cancelled, cannot confirm", ctx.getTransactionId());
            return;
        }
        ctx.setStatus(TccStatus.CONFIRMED);
        log.debug("TCC transaction [{}] confirmed", ctx.getTransactionId());
    }

    /**
     * Mark the current transaction as cancelled.
     */
    public void cancel(TransactionContext ctx) {
        if (ctx == null) {
            return;
        }
        if (ctx.getStatus() == TccStatus.CONFIRMED) {
            log.warn("TCC transaction [{}] already confirmed, cannot cancel", ctx.getTransactionId());
            return;
        }
        ctx.setStatus(TccStatus.CANCELLED);
        log.debug("TCC transaction [{}] cancelled", ctx.getTransactionId());
    }

    /**
     * Clean up the thread-local context after the transaction completes.
     */
    public void cleanup(TransactionContext ctx) {
        if (ctx == null) {
            return;
        }
        log.debug("TCC transaction [{}] cleaned up, final status: {}", ctx.getTransactionId(), ctx.getStatus());
        TransactionContext.clear();
    }
}
