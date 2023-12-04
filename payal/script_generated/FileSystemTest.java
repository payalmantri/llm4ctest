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
import org.apache.hadoop.fs.FsServerDefaults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class FileSystemTest {

    private Configuration conf;
    private FileSystem fileSystem;

    @Before
    public void setUp() throws IOException {
        conf = new Configuration();
        conf.addResource("core-default.xml");
        fileSystem = FileSystem.get(conf);
    }

    @Test
    public void testGetServerDefaults() throws IOException {
        FsServerDefaults serverDefaults = fileSystem.getServerDefaults();
        long expectedBlockSize = conf.getLong(
                "dfs.blocksize",
                128 * 1024 * 1024);
        long expectedBytesPerChecksum = conf.getLong(
                "io.bytes.per.checksum",
                512);

        Assert.assertEquals(expectedBlockSize, serverDefaults.getBlockSize());
        Assert.assertEquals(expectedBytesPerChecksum, serverDefaults.getBytesPerChecksum());
    }
}