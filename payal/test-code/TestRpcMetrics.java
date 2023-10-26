package org.apache.hadoop.ipc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.Server;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class TestRpcMetrics {

    private Configuration conf;
    private Server mockServer;

    @Before
    public void setUp() {
        conf = new Configuration();
        mockServer = Mockito.mock(Server.class); // Create a mock Server
    }

    @Test
    public void testRpcMetricsWithQuantileEnabled() {
        // Set configurations
        conf.set("rpc.metrics.quantile.enable", "true");
        conf.set("rpc.metrics.percentiles.intervals", "10,20,30");

        // Create RpcMetrics instance with the current configuration and mock server
        RpcMetrics metrics = new RpcMetrics(mockServer, conf);

        // Check if rpcEnQueueTimeQuantiles reflects the intervals set
        assertNotNull(metrics.rpcEnQueueTimeQuantiles);
        assertEquals(3, metrics.rpcEnQueueTimeQuantiles.length);
    }

    @Test
    public void testRpcMetricsWithQuantileDisabled() {
        // Set configuration for intervals but disable quantiles
        conf.set("rpc.metrics.quantile.enable", "false");
        conf.set("rpc.metrics.percentiles.intervals", "10,20,30");

        // Create RpcMetrics instance with the current configuration and mock server
        RpcMetrics metrics = new RpcMetrics(mockServer, conf);

        // Check if rpcEnQueueTimeQuantiles is empty or null even if intervals are set
        if (metrics.rpcEnQueueTimeQuantiles != null) {
            assertEquals(0, metrics.rpcEnQueueTimeQuantiles.length);
        }
    }

    // ... Add other test methods as necessary
}
