package com.symplifica.backend.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "mySecretThatIsShorterThan32Bytes";
    private final long expirationMs = 10000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void deriveKeyBytes_ShouldPadSecretTo32Bytes_WhenShorter() throws Exception {
        Method deriveKeyBytes = JwtService.class.getDeclaredMethod("deriveKeyBytes", String.class);
        deriveKeyBytes.setAccessible(true);

        String shortSecret = "short"; // 5 bytes
        byte[] result = (byte[]) deriveKeyBytes.invoke(jwtService, shortSecret);

        assertThat(result).hasSize(32);
        // "short" repeated: shortshortshortshortshortshorts
        String expectedPadding = "shortshortshortshortshortshortsho".substring(0, 32);
        assertThat(new String(result)).isEqualTo(expectedPadding);
    }

    @Test
    void deriveKeyBytes_ShouldNotPad_When32BytesOrLonger() throws Exception {
        Method deriveKeyBytes = JwtService.class.getDeclaredMethod("deriveKeyBytes", String.class);
        deriveKeyBytes.setAccessible(true);

        String exactSecret = "12345678901234567890123456789012"; // 32 bytes
        byte[] resultExact = (byte[]) deriveKeyBytes.invoke(jwtService, exactSecret);
        assertThat(resultExact).hasSize(32);
        assertThat(new String(resultExact)).isEqualTo(exactSecret);

        String longSecret = "12345678901234567890123456789012345"; // 35 bytes
        byte[] resultLong = (byte[]) deriveKeyBytes.invoke(jwtService, longSecret);
        assertThat(resultLong).hasSize(35);
        assertThat(new String(resultLong)).isEqualTo(longSecret);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails userDetails = new User("test@test.com", "password", Collections.emptyList());
        
        String token = jwtService.generateToken(userDetails);
        
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@test.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDiffers() {
        UserDetails user1 = new User("test1@test.com", "password", Collections.emptyList());
        UserDetails user2 = new User("test2@test.com", "password", Collections.emptyList());
        
        String token = jwtService.generateToken(user1);
        
        assertThat(jwtService.isTokenValid(token, user2)).isFalse();
    }

    @Test
    void isTokenValid_ShouldThrowException_WhenTokenExpired() {
        // Creamos un servicio con expiración muy corta (-1 ms para que expire inmediatamente)
        JwtService shortLivedJwtService = new JwtService(secret, -1000L);
        UserDetails userDetails = new User("test@test.com", "password", Collections.emptyList());
        
        String token = shortLivedJwtService.generateToken(userDetails);

        assertThatThrownBy(() -> shortLivedJwtService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
