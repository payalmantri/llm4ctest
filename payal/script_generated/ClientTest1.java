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
import org.apache.hadoop.ipc.Client;
import org.apache.hadoop.ipc.Client.ConnectionId;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.net.InetSocketAddress;
import java.io.IOException;

public class ClientTest {
    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
        conf.addResource("core-default.xml");
    }

    @Test
    public void testClientConnectRetryIntervalConfiguration() throws IOException {
        InetSocketAddress addr = new InetSocketAddress("localhost", conf.getInt("port", 8050));
        Class<?> protocol = Object.class;
        UserGroupInformation ticket = UserGroupInformation.getCurrentUser();
        int rpcTimeout = conf.getInt("rpcTimeout", 10000);
        int maxRetries = conf.getInt(CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_KEY, CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_DEFAULT);
        long retryInterval = conf.getLong(CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_RETRY_INTERVAL_KEY, CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_RETRY_INTERVAL_DEFAULT);
        RetryPolicy retryPolicy = RetryPolicies.retryUpToMaximumCountWithFixedSleep(maxRetries, retryInterval, TimeUnit.MILLISECONDS);

        ConnectionId actual = Client.ConnectionId.getConnectionId(addr, protocol, ticket, rpcTimeout, retryPolicy, conf);
        Assert.assertNotNull(actual);
    }
}