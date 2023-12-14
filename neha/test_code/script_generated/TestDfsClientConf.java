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
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class TestDfsClientConf {

    private Configuration conf;
    private DFSClient dfsClient;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        conf = new Configuration();
        conf.addResource("hdfs-default.xml");
        URI nameNodeURI = new URI("hdfs://localhost:8020");
        dfsClient = new DFSClient(nameNodeURI, conf);
    }

    @Test
    public void testSlowIoWarningThreshold() {
        long expectedThreshold = Long.parseLong(conf.get("dfs.client.slow.io.warning.threshold.ms"));
        Assert.assertEquals(expectedThreshold, dfsClient.getConf().getSlowIoWarningThresholdMs());
    }
}