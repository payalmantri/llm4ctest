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
import org.junit.Test;
import org.junit.Assert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Timer;

public class GroupsTest {

 @Test
 public void testGroupsConstructor() {
 Configuration conf = new Configuration();
 Timer timer = new Timer();

 conf.set("hadoop.security.groups.cache.background.reload", "true");
 conf.set("hadoop.security.groups.cache.background.reload.threads", "2");

 Groups groups = new Groups(conf, timer);

 Assert.assertEquals("true", groups.getConf().get("hadoop.security.groups.cache.background.reload"));
 Assert.assertEquals("2", groups.getConf().get("hadoop.security.groups.cache.background.reload.threads"));
 }
}
