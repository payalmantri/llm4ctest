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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HttpServer2Test {
    private Configuration conf;
    private HttpServer2.Builder builder;
    private static final String HTTP_IDLE_TIMEOUT_KEY = "hadoop.http.idle_timeout.ms";
    private static final long DEFAULT_HTTP_IDLE_TIMEOUT_MS = 60000;

    @Before
    public void setup() {
        conf = new Configuration();
        builder = new HttpServer2.Builder().setConf(conf);
    }

    @Test
    public void testHttpIdleTimeout() throws IOException {
        conf.setInt(HTTP_IDLE_TIMEOUT_KEY, 70000);
        HttpServer2 httpServer2 = builder.build();
        long httpIdleTimeout = conf.getInt(HTTP_IDLE_TIMEOUT_KEY, 
                                (int) DEFAULT_HTTP_IDLE_TIMEOUT_MS);
        assertEquals(70000, httpIdleTimeout);
    }

    @Test
    public void testHttpIdleTimeoutDefault() throws IOException {
        HttpServer2 httpServer2 = builder.build();
        long httpIdleTimeout = conf.getInt(HTTP_IDLE_TIMEOUT_KEY, 
                                (int) DEFAULT_HTTP_IDLE_TIMEOUT_MS);
        assertEquals(DEFAULT_HTTP_IDLE_TIMEOUT_MS, httpIdleTimeout);
    }
}