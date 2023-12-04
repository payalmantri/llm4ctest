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
import org.apache.hadoop.security.Groups;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GroupsTest {
    private Configuration conf;
    private Groups groups;
    
    @Before
    public void setup(){
        conf = new Configuration();
        groups = new Groups(conf);
    }
    
    @Test
    public void testNoneGroupMapping() {
        conf.set("hadoop.security.group.mapping", "org.apache.hadoop.security.NullGroupsMapping");
        String result = conf.get("hadoop.security.group.mapping");
        assertEquals("org.apache.hadoop.security.NullGroupsMapping", result);
    }
    
    @Test
    public void testShellBasedUnixGroupsMapping() {
        conf.set("hadoop.security.group.mapping", "org.apache.hadoop.security.ShellBasedUnixGroupsMapping");
        String result = conf.get("hadoop.security.group.mapping");
        assertEquals("org.apache.hadoop.security.ShellBasedUnixGroupsMapping", result);
    }
    
    @Test
    public void testDefaultGroupMapping() {
        String result = conf.get("hadoop.security.group.mapping");
        assertEquals("org.apache.hadoop.security.ShellBasedUnixGroupsMapping", result);
    }
    
    @Test
    public void testIncorrectGroupMapping() {
        conf.set("hadoop.security.group.mapping", "org.apache.hadoop.security.NonExistentGroupMapping");
        String result = conf.get("hadoop.security.group.mapping");
        assertNotEquals("org.apache.hadoop.security.ShellBasedUnixGroupsMapping", result);
    }
}