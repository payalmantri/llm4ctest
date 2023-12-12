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
import org.apache.hadoop.security.LdapGroupsMapping;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LdapGroupsMappingTest {
  private Configuration conf;
  private LdapGroupsMapping ldapGroupsMapping;

  @Before
  public void setUp() {
    conf = new Configuration();
    conf.addResource("core-default.xml");
    ldapGroupsMapping = new LdapGroupsMapping();
  }

  @Test
  public void testSetConf() {
    ldapGroupsMapping.setConf(conf);
    String expectedGidName = conf.get("hadoop.security.group.mapping.ldap.posix.attr.gid.name");
    String actualGidName = ldapGroupsMapping.getConf().get("hadoop.security.group.mapping.ldap.posix.attr.gid.name");

    Assert.assertEquals(expectedGidName, actualGidName);
  }
}