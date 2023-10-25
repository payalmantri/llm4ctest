/*
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

package org.apache.hadoop.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class LineReaderTest {

    private Configuration conf;

    @Before
    public void setUp() {
        conf = new Configuration();
    }

    @Test
    public void testIoFileBufferSizeConfig() throws IOException {
        // Set the io.file.buffer.size configuration parameter
        int bufferSize = 4096; // You can set this to any desired value
        conf.setInt("io.file.buffer.size", bufferSize);

        // Create a sample input stream for testing
        String testString = "This is a test string for LineReader.";
        InputStream in = new ByteArrayInputStream(testString.getBytes());

        // Create a LineReader instance with the configuration
        LineReader lineReader = new LineReader(in, conf);

        // Read a line from the LineReader
        Text line = new Text();
        int bytesRead = lineReader.readLine(line);

        // Assert that the bytes read is as expected
        assertEquals(testString.length(), bytesRead);

        // Use reflection to access the protected getBufferSize() method
        try {
            Method getBufferSizeMethod = LineReader.class.getDeclaredMethod("getBufferSize");
            getBufferSizeMethod.setAccessible(true);
            int actualBufferSize = (int) getBufferSizeMethod.invoke(lineReader);
            
            // Assert that the buffer size used by LineReader is as set in the configuration
            assertEquals(bufferSize, actualBufferSize);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access getBufferSize() using reflection", e);
        }

        // Close the LineReader
        lineReader.close();
    }
}