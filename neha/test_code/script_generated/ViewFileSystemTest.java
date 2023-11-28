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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.hadoop.conf.Configuration;

public class ViewFileSystemTest {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @After
    public void tearDown() {
        conf.clear();
        conf = null;
    }

    @Test
    public void testRenameStrategy_SameMountPoint() {
        conf.set("fs.viewfs.rename.strategy", "SAME_MOUNTPOINT");

        assertEquals("SAME_MOUNTPOINT", conf.get("fs.viewfs.rename.strategy"));
    }

    @Test
    public void testRenameStrategy_SameTargetUriAcrossMountPoint() {
        conf.set("fs.viewfs.rename.strategy", "SAME_TARGET_URI_ACROSS_MOUNTPOINT");

        assertEquals("SAME_TARGET_URI_ACROSS_MOUNTPOINT", conf.get("fs.viewfs.rename.strategy"));
    }

    @Test
    public void testRenameStrategy_SameFileSystemAcrossMountPoint() {
        conf.set("fs.viewfs.rename.strategy", "SAME_FILESYSTEM_ACROSS_MOUNTPOINT");

        assertEquals("SAME_FILESYSTEM_ACROSS_MOUNTPOINT", conf.get("fs.viewfs.rename.strategy"));
    }

    @Test
    public void testRenameStrategy_Invalid() {
        conf.set("fs.viewfs.rename.strategy", "INVALID_STRATEGY");

        assertNotEquals("INVALID_STRATEGY", conf.get("fs.viewfs.rename.strategy"));
    }
}