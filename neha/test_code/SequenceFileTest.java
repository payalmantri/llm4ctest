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

package org.apache.hadoop.io;

import static org.junit.Assert.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

public class SequenceFileTest {

    @Test
    public void testSerializationConfiguration() {
        // Create a Configuration object
        Configuration conf = new Configuration();

        // Set the io.serializations property to the desired value
        conf.set("io.serializations",
                "org.apache.hadoop.io.serializer.WritableSerialization, org.apache.hadoop.io.serializer.avro.AvroSpecificSerialization, org.apache.hadoop.io.serializer.avro.AvroReflectSerialization");

        // Create an instance of SequenceFile using the configuration
        try {
            // Create a temporary directory for testing
            String testDir = "/tmp/sequencefile-test"; // Adjust the directory as needed
            FileSystem fs = FileSystem.get(conf);
            Path dirPath = new Path(testDir);
            if (!fs.exists(dirPath)) {
                fs.mkdirs(dirPath);
            }
            
            Path filePath = new Path(dirPath, "test.seq"); // Specify a file within the directory
            SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, filePath, null, null);
            assertNotNull(writer);
        } catch (Exception e) {
            fail("Failed to create SequenceFile.Writer with custom io.serializations configuration: " + e.getMessage());
        }
    }
}



