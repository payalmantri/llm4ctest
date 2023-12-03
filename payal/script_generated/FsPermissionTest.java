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

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Test;

public class FsPermissionTest {
    @Test
    public void testGetUMask() {
        Configuration conf = new Configuration();

        // Set Umask values in configuration
        conf.set("dfs.umaskmode", "000");
        FsPermission result = FsPermission.getUMask(conf);
        assertEquals(new FsPermission((short) 0), result);

        conf.set("dfs.umaskmode", "022");
        result = FsPermission.getUMask(conf);
        assertEquals(new FsPermission((short) 18), result);

        conf.set("dfs.umaskmode", "777");
        result = FsPermission.getUMask(conf);
        assertEquals(new FsPermission((short) 511), result);

        conf.unset("dfs.umaskmode");
        result = FsPermission.getUMask(conf);
        assertEquals(new FsPermission((short) 18), result);
    }
}