package org.apache.hadoop.llmgenerated;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ShutdownHookManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ShutdownTimeoutTest {

    private Thread longRunningHook;
    private long configuredTimeout;

    @Before
    public void setup() {
        // 1. Set the configuration parameter for a shorter timeout.
        Configuration conf = new Configuration();
        conf.set("hadoop.service.shutdown.timeout", "5s");

        // Verify the timeout was picked up by ShutdownHookManager
        configuredTimeout = ShutdownHookManager.getShutdownTimeout(conf);
        assertEquals(5, configuredTimeout);  // 5 seconds in milliseconds

        // 2. Add a long running shutdown hook.
        longRunningHook = new Thread(() -> {
            try {
                // Simulate a long shutdown process that takes 20 seconds.
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                // This should be caught if the shutdown is forced before 20 seconds.
            }
        });

        ShutdownHookManager.get().addShutdownHook(longRunningHook, 0);
    }

    @After
    public void cleanup() {
        // Cleanup any shutdown hooks that might have been left over.
        ShutdownHookManager.get().removeShutdownHook(longRunningHook);
    }

    @Test
    public void testShutdownTimeoutBehavior() throws InterruptedException {
        // Start the long running shutdown hook.
        longRunningHook.start();

        // Wait for 10 seconds (5 seconds more than the expected timeout).
        Thread.sleep(10 * 1000);

        // Assert that the long running hook was interrupted and has finished.
        assertTrue(!longRunningHook.isAlive());
    }
}
