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
import org.apache.hadoop.ha.FailoverController;
import org.apache.hadoop.ha.HAServiceTarget;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Mock;

public class FailoverControllerTest {
  private Configuration conf;

  @Mock
  private HAServiceTarget target;

  @Before
  public void setUp() {
    conf = new Configuration();
    Mockito.when(target.getHAServiceProtocol()).thenReturn(HAServiceProtocol.RequestSource.REQUEST_BY_USER);
  }

  @Test
  public void testRpcTimeoutConfiguration() {
    conf.set("ha.failover-controller.new-active.rpc-timeout.ms", "40000");
    FailoverController fc = new FailoverController(conf, target);
    Assert.assertEquals(40000, conf.getInt("ha.failover-controller.new-active.rpc-timeout.ms", 0));
  }

  @Test
  public void testDefaultRpcTimeoutConfiguration() {
    FailoverController fc = new FailoverController(conf, target);
    Assert.assertEquals(60000, conf.getInt("ha.failover-controller.new-active.rpc-timeout.ms", 0));
  }
}