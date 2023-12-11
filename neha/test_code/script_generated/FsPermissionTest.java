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
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;

public class FsPermissionTest {

    @Test
    public void testUMask() {
        Configuration conf = new Configuration();

        // default
        conf.set("fs.permissions.umask-mode", "022");
        FsPermission permission = FsPermission.getUMask(conf);
        Assert.assertEquals(Short.parseShort("022", 8), permission.toShort());

        // entirely open (dangerous)
        conf.set("fs.permissions.umask-mode", "000");
        permission = FsPermission.getUMask(conf);
        Assert.assertEquals(Short.parseShort("000", 8), permission.toShort());

        // entirely locked down (a bit overkill)
        conf.set("fs.permissions.umask-mode", "777");
        permission = FsPermission.getUMask(conf);
        Assert.assertEquals(Short.parseShort("777", 8), permission.toShort());

        // introducing a misconfiguration
        try {
            conf.set("fs.permissions.umask-mode", "xyz");
            permission = FsPermission.getUMask(conf);
            Assert.fail("Expected IllegalArgumentException for invalid umask");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}