package org.apache.hadoop.security;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestSecurityUtilConfiguration {

    private Configuration conf;

    @Before
    public void setUp() {
        // Create a fresh Configuration instance for each test
        conf = new Configuration();
        // Set the test configuration for SecurityUtil
        SecurityUtil.setConfiguration(conf);
    }

    @Test
    public void testLoggingEnabledWithDefaultThreshold() throws UnknownHostException {
        conf.setBoolean("hadoop.security.dns.log-slow-lookups.enabled", true);

        // This call is to invoke SecurityUtil's method with the current configuration.
        InetAddress address = SecurityUtil.getByName("localhost");
        Assert.assertNotNull(address);
        
        Assert.assertTrue(conf.getBoolean("hadoop.security.dns.log-slow-lookups.enabled", false));
        // Assuming a default threshold is 1000ms (adjust as necessary)
        Assert.assertEquals(1000, conf.getInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 1000));
    }

    @Test
    public void testLoggingDisabledWithThreshold() throws UnknownHostException {
        conf.setBoolean("hadoop.security.dns.log-slow-lookups.enabled", false);
        conf.setInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 1000);

        InetAddress address = SecurityUtil.getByName("localhost");
        Assert.assertNotNull(address);
        
        Assert.assertFalse(conf.getBoolean("hadoop.security.dns.log-slow-lookups.enabled", true));
        Assert.assertEquals(1000, conf.getInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 0));
    }

    @Test
    public void testLoggingEnabledWithCustomThreshold() throws UnknownHostException {
        conf.setBoolean("hadoop.security.dns.log-slow-lookups.enabled", true);
        conf.setInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 3000);

        InetAddress address = SecurityUtil.getByName("localhost");
        Assert.assertNotNull(address);
        
        Assert.assertTrue(conf.getBoolean("hadoop.security.dns.log-slow-lookups.enabled", false));
        Assert.assertEquals(3000, conf.getInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 0));
    }

    @Test
    public void testLoggingDisabledByDefault() throws UnknownHostException {
        InetAddress address = SecurityUtil.getByName("localhost");
        Assert.assertNotNull(address);
        
        Assert.assertFalse(conf.getBoolean("hadoop.security.dns.log-slow-lookups.enabled", false));
        Assert.assertEquals(1000, conf.getInt("hadoop.security.dns.log-slow-lookups.threshold.ms", 1000));
    }

    // Additional tests can be added to test more edge cases and behaviors.
}
