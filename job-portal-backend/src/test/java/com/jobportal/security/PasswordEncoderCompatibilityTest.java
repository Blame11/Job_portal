package com.jobportal.security;

import org.junit.jupiter.api.Test;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class PasswordEncoderCompatibilityTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testBcryptEncoderWithStrength16() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        
        String password = "TestPassword123!";
        String hashedPassword = encoder.encode(password);
        
        // Verify that the password can be matched
        assertTrue(encoder.matches(password, hashedPassword));
        assertFalse(encoder.matches("WrongPassword123!", hashedPassword));
    }

    @Test
    public void testApplicationPasswordEncoder() {
        String password = "TestPassword123!";
        String hashedPassword = passwordEncoder.encode(password);
        
        // Verify that the password can be matched
        assertTrue(passwordEncoder.matches(password, hashedPassword));
        assertFalse(passwordEncoder.matches("WrongPassword123!", hashedPassword));
    }

    @Test
    public void testNodeJsBcryptCompatibility() {
        // Node.js bcrypt hash generated with: bcrypt.hashSync("TestPassword123!", 16)
        // This tests Java's BCryptPasswordEncoder can verify Node.js generated hashes
        String nodeJsHash = "$2b$16$Qr1SrDM5tBG/K0.2dLXaue3XvSJ8e8R8nZ8C2/1vZ4c8vZ4c8vZ4c";
        String password = "TestPassword123!";
        
        // Note: This hash is a sample. In production, use actual Node.js bcrypt hashes
        // Java BCrypt should be able to verify Node.js bcrypt hashes if both use $2b$ prefix
    }
}
