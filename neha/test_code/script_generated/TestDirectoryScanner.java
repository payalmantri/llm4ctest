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
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.DirectoryScanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDirectoryScanner {

    private Configuration conf;
    private MiniDFSCluster cluster;
    private FsDatasetSpi<?> fds;
    private DirectoryScanner directoryScanner;

    @Before
    public void setUp() throws Exception {
        conf = new Configuration();
        cluster = new MiniDFSCluster.Builder(conf).build();
        cluster.waitActive();
        fds = DataNodeTestUtils.getFSDataset(cluster.getDataNodes().get(0));
        directoryScanner = new DirectoryScanner(fds, conf);
        directoryScanner.setScanPeriod(3600L, 0L, 3600L, 0L, 0L, 0L);
    }

    @Test
    public void testDirectoryScannerConfiguration() throws Exception {
        int directoryScanThreads = conf.getInt(DFSConfigKeys.DFS_DATANODE_DIRECTORYSCAN_THREADS_KEY, 1);
        Assert.assertEquals(directoryScanThreads, directoryScanner.getParallelism());
    }
}