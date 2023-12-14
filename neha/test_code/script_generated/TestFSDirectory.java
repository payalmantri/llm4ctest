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
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

public class TestFSDirectory {
    private Configuration conf;
    private FSNamesystem fsNameSystem;

    @Before
    public void setUp() {
        conf = new Configuration();
        fsNameSystem = FSNamesystem.loadFromDisk(conf);
    }

    @Test
    public void testMaxXattrSizeConfiguration() throws IOException {
        FSDirectory directory = fsNameSystem.getFSDirectory();
        String maxSizeStr = conf.get("dfs.namenode.fs-limits.max-xattr-size");
        if (maxSizeStr != null) {
            int maxSize = Integer.parseInt(maxSizeStr);
            Assert.assertEquals(maxSize, directory.getXAttrMaxSize());
        }
    }
}