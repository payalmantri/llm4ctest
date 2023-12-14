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
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestDFSUtilClient {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
        conf.set(DFSConfigKeys.DFS_USER_HOME_DIR_PREFIX_KEY, "/user");
    }

    @Test
    public void testHomeDirectoryConfiguration() throws IOException {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        
        String homeDirectory = DFSUtilClient.getHomeDirectory(conf, ugi);
        String expectedHomeDirectory = conf.get(DFSConfigKeys.DFS_USER_HOME_DIR_PREFIX_KEY) + "/" + ugi.getShortUserName();
        
        Assert.assertEquals(expectedHomeDirectory, homeDirectory);
    }
}