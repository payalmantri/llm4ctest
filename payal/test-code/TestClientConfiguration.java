package org.apache.hadoop.ipc;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestClientConfiguration {

    private Configuration conf;

    @Before
    public void setup() {
        conf = new Configuration();
    }

    @Test
    public void testGetTimeoutWithClientPing() {
        // set the ipc.client.ping configuration
        conf.set("ipc.client.ping", "true");
        
        // when ipc.client.ping is true, it should use the ipc.ping.interval for timeout
        conf.set("ipc.ping.interval", "5000");
        assertEquals(5000, Client.getTimeout(conf));

        conf.set("ipc.ping.interval", "10000");
        assertEquals(10000, Client.getTimeout(conf));
    }

    @Test
    public void testGetTimeoutWithoutClientPing() {
        // set the ipc.client.ping configuration
        conf.set("ipc.client.ping", "false");
        
        // when ipc.client.ping is false, it should return 0 regardless of ipc.ping.interval value
        conf.set("ipc.ping.interval", "5000");
        assertEquals(0, Client.getTimeout(conf));

        conf.set("ipc.ping.interval", "10000");
        assertEquals(0, Client.getTimeout(conf));
    }

    @Test
    public void testGetTimeoutDefaultValues() {
        // when no configurations are set, check the default behavior
        assertEquals(0, Client.getTimeout(conf)); // Assuming 0 is the default timeout when parameters are not set
    }

}
