package org.apache.hadoop.security;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation.AuthenticationMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SecurityUtilTest {

    @Test
    public void testGetAuthenticationMethodWithValidParameter() {
        // Initialize a new Configuration object
        Configuration conf = new Configuration();

        // Define expected and actual AuthenticationMethod
        AuthenticationMethod expectedMethod = AuthenticationMethod.KERBEROS;
        AuthenticationMethod actualMethod;

        // Set the 'hadoop.security.authentication' configuration parameter
        conf.set("hadoop.security.authentication", expectedMethod.toString().toLowerCase());

        // Get the authentication method using SecurityUtil
        actualMethod = SecurityUtil.getAuthenticationMethod(conf);

        // Assert that the expected and actual AuthenticationMethod are equal
        assertEquals("Expected and actual AuthenticationMethod should be equal", expectedMethod, actualMethod);
    }

    @Test
    public void testGetAuthenticationMethodWithInvalidParameter() {
        // Initialize a new Configuration object
        Configuration conf = new Configuration();

        // Set the 'hadoop.security.authentication' configuration parameter to an invalid value
        conf.set("hadoop.security.authentication", "invalidMethod");

        try {
            // Try to get the authentication method using SecurityUtil
            SecurityUtil.getAuthenticationMethod(conf);

            // If no exception was thrown, make the test fail
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // Assert that the exception message is as expected
            assertEquals("Unexpected exception message", "Invalid attribute value for hadoop.security.authentication of invalidMethod", e.getMessage());
        }
    }
}
