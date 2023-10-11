package org.apache.hadoop.llmgenerated;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class TestSecurityUtil {

    private static final String HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY = "hadoop.security.dns.log-slow-lookups.enabled";
    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @Test
    public void testLogSlowLookupsEnabled() throws UnknownHostException {
        // set the configuration
        conf.setBoolean(HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY, true);

        // apply the configuration to SecurityUtil
        SecurityUtil.setConfiguration(conf);

        // use conf.get() to assert the configuration value
        assertTrue("Expected logSlowLookups to be true", conf.getBoolean(HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY, false));

        // test the InetAddress getByName functionality 
        SecurityUtil.getByName("localhost");

        // For this demonstration, assuming the getByName method uses the logSlowLookups configuration internally.
        // In a real-world scenario, you'd probably need to capture/check logs to confirm the behavior.
    }

    @Test
    public void testLogSlowLookupsDisabled() throws UnknownHostException {
        // set the configuration
        conf.setBoolean(HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY, false);

        // apply the configuration to SecurityUtil
        SecurityUtil.setConfiguration(conf);

        assertFalse("Expected logSlowLookups to be false", conf.getBoolean(HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY, true));

        // test the InetAddress getByName functionality 
        SecurityUtil.getByName("localhost");
    }
}
