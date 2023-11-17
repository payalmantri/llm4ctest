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

package org.apache.hadoop.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;
import org.junit.Assert;
import org.junit.Test;

public class BLineReaderTest {

    @Test
    public void testReadLineWithCustomBufferSize() throws IOException {
        // Create a Configuration object and set the io.file.buffer.size property to 1024
        Configuration conf = new Configuration();
        conf.setInt("io.file.buffer.size", 1024);

        // Create a LineReader object using the custom Configuration
        LineReader lineReader = new LineReader(new ByteArrayInputStream("Hello\nWorld".getBytes()), conf);

        // Read a line from the input stream
        Text line = new Text();
        int bytesRead = lineReader.readLine(line);

        // Assert that the read line is "Hello"
        Assert.assertEquals("Hello", line.toString());

        // Assert that the number of bytes read is 5
        Assert.assertEquals(5, bytesRead);
    }

    @Test
    public void testReadLineWithDefaultBufferSize() throws IOException {
        // Create a LineReader object using the default buffer size (64K)
        LineReader lineReader = new LineReader(new ByteArrayInputStream("Hello\nWorld".getBytes()));

        // Read a line from the input stream
        Text line = new Text();
        int bytesRead = lineReader.readLine(line);

        // Assert that the read line is "Hello"
        Assert.assertEquals("Hello", line.toString());

        // Assert that the number of bytes read is 5
        Assert.assertEquals(5, bytesRead);
    }
}
