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
import org.apache.hadoop.net.NetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

public class TestDFSUtil {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @Test
    public void testRpcServiceAddressConfig() throws Exception {
        Map<String, Map<String, InetSocketAddress>> rpcAddresses = DFSUtil.getNNServiceRpcAddresses(conf);

        for (String nsId : rpcAddresses.keySet()) {
            for (String nnId : rpcAddresses.get(nsId).keySet()) {
                URI expectedUri = URI.create(conf.get(DFSConfigKeys.DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY + "." + nsId + "." + nnId));
                InetSocketAddress expectedAddress = NetUtils.createSocketAddr(expectedUri.toString());
                Assert.assertEquals(expectedAddress, rpcAddresses.get(nsId).get(nnId));
            }
        }
    }
}