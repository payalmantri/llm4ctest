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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestDFSClient {
    
    private Configuration conf;
    private ClientProtocol rpcNamenode;
    private FileSystem.Statistics stats;
    private URI nameNodeUri;

    @Before
    public void setUp() {
        conf = new Configuration();
        rpcNamenode = null; // setup accordingly
        stats = null; // setup accordingly
        nameNodeUri = URI.create(""); // setup accordingly
    }

    @Test
    public void testDataTransferConfiguration() throws IOException {
        DFSClient client = new DFSClient(nameNodeUri, rpcNamenode, conf, stats);
        String[] localInterfaces = conf.get("dfs.client.local.interfaces").split(",");
        Set<String> interfaceSet = new HashSet<>(Arrays.asList(localInterfaces));
        Assert.assertTrue(interfaceSet.contains(client.getConf().get("dfs.client.local.interfaces")));
    }
}