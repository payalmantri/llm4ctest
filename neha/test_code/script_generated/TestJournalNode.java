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
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.qjournal.server.JournalNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class JournalNodeTest {
    private static final String NN1_ADDRESS = "localhost:9000";
    private static final String NN2_ADDRESS = "localhost:9001";
    private Configuration conf;
    private JournalNode journalNode;

    @Before
    public void setup() {
        conf = new Configuration();
        journalNode = new JournalNode();
    }

    @Test
    public void testSetConf() {
        conf.set(DFSConfigKeys.DFS_NAMESERVICES, "nn1,nn2");
        conf.set(DFSConfigKeys.DFS_INTERNAL_NAMESERVICES_KEY, "nn1");
        conf.set(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, "nn1"), NN1_ADDRESS);
        conf.set(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY, "nn2"), NN2_ADDRESS);
        journalNode.setConf(conf);

        try {
            Collection<String> all = DFSUtilClient.getNameServiceIds(journalNode.getConf());
            Assert.assertEquals(new HashSet<>(Arrays.asList("nn1", "nn2")), all);
            Map<String, Map<String, InetSocketAddress>> nnMap = DFSUtil.getNNServiceRpcAddressesForCluster(journalNode.getConf());
            Assert.assertEquals(1, nnMap.size());
            Assert.assertTrue(nnMap.containsKey("nn1"));
        } catch (IOException e) {
            Assert.fail("This should not have thrown an exception");
        }

        journalNode.getConf().set(DFSConfigKeys.DFS_INTERNAL_NAMESERVICES_KEY, "nn3");
        
        try {
            DFSUtil.getNNServiceRpcAddressesForCluster(conf);
            Assert.fail("This should have thrown an exception");
        } catch (IOException ignored) {}
    }
}