
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mockito;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FSDataInputStreamTest {

 private FSDataInputStream fsDataInputStream;
 private Path file;
 private int bufferSize;

 @Before
 public void setUp() {
 System.setProperty("fs.ftp.data.connection.mode", "PASSIVE");
 file = new Path("/test/path");
 bufferSize = 1024;
 fsDataInputStream = Mockito.mock(FSDataInputStream.class);
 }

 @Test
 public void testOpen() throws IOException {
 try {
 fsDataInputStream.open(file, bufferSize);
 } catch (IOException e) {
 fail("IOException thrown: " + e.getMessage());
 }
 verify(fsDataInputStream, times(1)).open(file, bufferSize);
 }

 @After
 public void tearDown() {
 System.clearProperty("fs.ftp.data.connection.mode");
 }
}
