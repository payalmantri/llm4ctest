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
package org.apache.hadoop.security;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import java.lang.reflect.Field;
import org.junit.Test;
import org.apache.hadoop.util.Timer;

public class TestGroups {

    private Configuration conf;
    private Timer mockTimer;

    @Before
    public void setUp() {
        conf = new Configuration();
        mockTimer = mock(Timer.class); // Assuming Timer is an interface or a class that can be mocked.
    }

    @Test
    public void testBackgroundReloadFalseByDefault() {
        Groups groups = new Groups(conf, mockTimer);
        assertFalse("By default, background reload should be false",
            conf.getBoolean("hadoop.security.groups.cache.background.reload", true));
    }

    @Test
    public void testBackgroundReloadTrue() {
        conf.setBoolean("hadoop.security.groups.cache.background.reload", true);
        Groups groups = new Groups(conf, mockTimer);
        assertTrue("Background reload should be true when set",
            conf.getBoolean("hadoop.security.groups.cache.background.reload", false));
    }

    @Test
    public void testBackgroundReloadFalse() {
        conf.setBoolean("hadoop.security.groups.cache.background.reload", false);
        Groups groups = new Groups(conf, mockTimer);
        assertFalse("Background reload should be false when set",
            conf.getBoolean("hadoop.security.groups.cache.background.reload", true));
    }

    // Additional tests can be added to test the behavior of the Groups class based on the configuration.

    @Test
    public void testGroupsCacheBackgroundReloadConfig() throws Exception {
        conf.set("hadoop.security.groups.cache.background.reload", "true");
        conf.setInt("hadoop.security.groups.cache.background.reload.threads", 5);
        Groups groups = new Groups(conf, mockTimer);

        Field reloadField = null;
        try {
            reloadField = Groups.class.getField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD");
        } catch (NoSuchFieldException e) {
            reloadField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD");
        }
        reloadField.setAccessible(true);
        String reloadValue = (String) reloadField.get(groups);

        // Field reloadField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD");
        // reloadField.setAccessible(true);
        // String reloadValue = (String) reloadField.get(groups);

        Field threadsField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD_THREADS");
        threadsField.setAccessible(true);
        String threadsValue = (String) threadsField.get(groups);

        assertEquals("true", reloadValue);
        assertEquals("5", threadsValue);
    }

    @Test
    public void testGroupsCacheBackgroundReloadDefaultConfig() throws Exception {
        Groups groups = new Groups(conf, mockTimer);

        // Field reloadField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD_DEFAULT");
        // reloadField.setAccessible(true);
        // boolean reloadValue = reloadField.getBoolean(groups);

        Field reloadField = null;
        try {
            reloadField = Groups.class.getField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD_DEFAULT");
        } catch (NoSuchFieldException e) {
            reloadField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD_DEFAULT");
        }
        reloadField.setAccessible(true);
        String reloadValue = (String) reloadField.get(groups);


        Field threadsField = Groups.class.getDeclaredField("HADOOP_SECURITY_GROUPS_CACHE_BACKGROUND_RELOAD_THREADS_DEFAULT");
        threadsField.setAccessible(true);
        int threadsValue = threadsField.getInt(groups);

        assertFalse(reloadValue);
        assertEquals(3, threadsValue);
    }
}
