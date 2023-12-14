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
import org.apache.hadoop.hdfs.DFSUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

public class TestDFSUtil {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @Test
    public void testGetJournalNodeAddresses() throws URISyntaxException, IOException {
        Set<String> expectedAddresses = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expectedAddresses, DFSUtil.getJournalNodeAddresses(conf));
    }

    @Test
    public void testGetJournalNodeAddressesWithConfigChange() throws URISyntaxException, IOException {
        String newConfigUrl = "hdfs://localhost:9000";
        conf.set("dfs.namenode.shared.edits.dir", newConfigUrl);
        Set<String> expectedAddresses = DFSUtil.getJournalNodeAddresses(conf);
        Assert.assertEquals(expectedAddresses, DFSUtil.getJournalNodeAddresses(conf));
    }
}