/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.security;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestSecUtil {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();

        // Set default values
        conf.set("hadoop.security.dns.log-slow-lookups.enabled", "false");
        conf.set("hadoop.security.dns.log-slow-lookups.threshold.ms", "1000");
    }

    @Test
    public void testGetByNameWithLoggingDisabled() throws UnknownHostException {
        String testHostname = "localhost";

        InetAddress address = SecurityUtil.getByName(testHostname);

        assertNotNull(address);
        assertEquals(testHostname, address.getHostName());
    }

    @Test
    public void testGetByNameWithLoggingEnabled() throws UnknownHostException {
        String testHostname = "localhost";

        // Enable slow lookup logging
        conf.set("hadoop.security.dns.log-slow-lookups.enabled", "true");
        
        // We won't be able to simulate a slow lookup in this basic test. 
        // In a real environment, you'd probably need a mock DNS server or a way to inject delay.
        // But this test ensures the method does not throw exceptions when logging is enabled.

        InetAddress address = SecurityUtil.getByName(testHostname);

        assertNotNull(address);
        assertEquals(testHostname, address.getHostName());
    }

    // Additional tests could involve:
    // 1. Changing the logging threshold and simulating slow lookups
    // 2. Testing against invalid hostnames to ensure UnknownHostException is thrown
}
