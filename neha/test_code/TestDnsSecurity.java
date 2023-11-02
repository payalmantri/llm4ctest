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

import static org.junit.Assert.*;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import java.net.UnknownHostException;

public class TestDnsSecurity {

    @Test
    public void testGetLocalHostNameWithValidConfig() throws UnknownHostException {
        Configuration conf = new Configuration();
        conf.set("hadoop.security.dns.nameserver", "8.8.8.8");
        conf.set("hadoop.security.dns.interface", "ens33"); // Updated to a valid network interface

        String hostName = SecurityUtil.getLocalHostName(conf);
        assertNotNull("Host name should not be null", hostName);
        // Add more assertions here to validate the host name
    }

    @Test
    public void testGetLocalHostNameWithNoConfig() throws UnknownHostException {
        Configuration conf = new Configuration();

        String hostName = SecurityUtil.getLocalHostName(conf);
        assertNotNull("Host name should not be null", hostName);
        // Add more assertions here to validate the host name
    }

    @Test
    public void testGetLocalHostNameWithOnlyNameserverConfig() {
        Configuration conf = new Configuration();
        conf.set("hadoop.security.dns.nameserver", "8.8.8.8");

        try {
            SecurityUtil.getLocalHostName(conf);
            fail("Expected an exception to be thrown");
        } catch (UnknownHostException | IllegalArgumentException e) {
            // Expected, as the interface is not set
        }
    }

    @Test
    public void testGetLocalHostNameWithOnlyInterfaceConfig() {
        Configuration conf = new Configuration();
        conf.set("hadoop.security.dns.interface", "eth0");

        try {
            SecurityUtil.getLocalHostName(conf);
            fail("Expected an exception to be thrown");
        } catch (UnknownHostException e) {
            // Expected, as the nameserver is not set
        }
    }
}
