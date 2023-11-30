/**
     * Licensed to the Apache Software Foundation (ASF) under one
     * or more contributor license agreements.  See the NOTICE file
     * distributed with this work for additional information
     * regarding copyright ownership.  The ASF licenses this file
     * to you under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance
     * with the License.  You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    package org.apache.hadoop.llmgenerated;

import static org.junit.Assert.*;
import org.apache.hadoop.conf.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.hadoop.security.SecurityUtil;

public class SecurityUtilTest {
    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @After
    public void tearDown() {
        conf = null;
    }

    @Test
    public void testSetConfiguration() {
        // Testing valid configuration
        conf.set("hadoop.security.dns.log-slow-lookups.threshold.ms", "1000");
        SecurityUtil.setConfiguration(conf);
        assertEquals("1000", conf.get("hadoop.security.dns.log-slow-lookups.threshold.ms"));

        // Testing invalid configuration
        conf.set("hadoop.security.dns.log-slow-lookups.threshold.ms", "-1");
        try {
            SecurityUtil.setConfiguration(conf);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("A negative value: -1 is not valid for property hadoop.security.dns.log-slow-lookups.threshold.ms", e.getMessage());
        }
    }
}