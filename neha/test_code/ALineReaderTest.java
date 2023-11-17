package org.apache.hadoop.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.LineReader;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ALineReaderTest {

    // Define logsContainWarning method
    private boolean logsContainWarning() {
        // Add your implementation to check if logs contain a warning
        // Return true if logs contain a warning, false otherwise
        return logContainsLevel("WARN");
    }

    @Test
    public void testLineReaderBufferSizeConfig() throws IOException {
        // Set up
        Configuration conf = new Configuration();

        // Test different buffer size values
        conf.set("io.file.buffer.size", "2048");

        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello world!".getBytes());

        // Exercise
        LineReader reader = new LineReader(inputStream, conf);

        // Verify
        Assert.assertEquals(2048, reader.getBufferSize());

        // Repeat with different buffer sizes
        conf.set("io.file.buffer.size", "4096");

        reader = new LineReader(inputStream, conf);

        Assert.assertEquals(4096, reader.getBufferSize());

        conf.set("io.file.buffer.size", "8192");

        reader = new LineReader(inputStream, conf);

        Assert.assertEquals(8192, reader.getBufferSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBufferSize() throws IOException {
        Configuration conf = new Configuration();

        // Invalid buffer size
        conf.set("io.file.buffer.size", "0");

        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello world!".getBytes());

        // Exercise and Verify
        // This should throw an IllegalArgumentException
        new LineReader(inputStream, conf);
    }

    @Test
    public void testNegativeBufferSize() throws IOException {
        Configuration conf = new Configuration();

        conf.set("io.file.buffer.size", "-1024");

        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello world!".getBytes());

        // Exercise
        LineReader reader = new LineReader(inputStream, conf);

        // Should log a warning
        assertTrue(logsContainWarning());
    }

    @Test
    public void testNonNumericBufferSize() throws IOException {
        Configuration conf = new Configuration();

        conf.set("io.file.buffer.size", "text");

        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello world!".getBytes());

        // Exercise
        LineReader reader = new LineReader(inputStream, conf);

        // Should log a warning
        assertTrue(logsContainWarning());
    }
}
