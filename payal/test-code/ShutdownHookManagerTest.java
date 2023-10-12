package org.apache.hadoop.util;

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

public class ShutdownHookManagerTest {

    private Configuration conf;

    @Before
    public void setup() {
        conf = new Configuration();
    }

    @Test
    public void testGetShutdownTimeout() {
        // Arrange
        long expectedTimeout = 10000L; // 10 seconds for instance
        conf.set("hadoop.service.shutdown.timeout", String.valueOf(expectedTimeout));

        // Act
        long actualTimeout = ShutdownHookManager.getShutdownTimeout(conf);

        // Assert
        assertEquals(expectedTimeout, actualTimeout);
    }

    // Additional test cases can be added as needed to validate different scenarios
    // such as what happens when the configuration parameter is not set, or is set to an invalid value.
}
