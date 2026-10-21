package com.replaymod.replay.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReplayPacketGate {

    private static final Logger LOGGER = LogManager.getLogger("ReplayPacketGate");
    private static final long TIMEOUT_MS = 5000L;

    private static volatile CountDownLatch latch = new CountDownLatch(0);
    private static volatile boolean expectingSync = false;

    private ReplayPacketGate() {}

    public static synchronized void reset() {
        latch = new CountDownLatch(1);
        expectingSync = true;
        LOGGER.debug("ReplayPacketGate reset - waiting for registry sync");
    }

    public static void markRegistrySyncComplete() {
        LOGGER.debug("Registry sync completed, releasing gate");
        latch.countDown();
    }

    public static void awaitRegistrySync() {
        if (!expectingSync) {
            return;
        }
        LOGGER.debug("Waiting for registry sync now...");
        try {
            boolean completed = latch.await(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!completed) {
                LOGGER.warn("Timed out waiting for registry sync ({}ms) - proceeding anyway.", TIMEOUT_MS);
            } else {
                LOGGER.debug("Registry sync wait completed successfully.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            expectingSync = false;
        }
    }
}