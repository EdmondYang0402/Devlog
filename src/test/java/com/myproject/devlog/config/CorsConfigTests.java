package com.myproject.devlog.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsConfigTests {
    @Test
    void allowsOnlyConfiguredOrigins() {
        UrlBasedCorsConfigurationSource source =
                new CorsConfig(
                        "http://localhost:5173,http://localhost:8088,https://*.trycloudflare.com")
                        .corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/user/register");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals("http://localhost:5173", config.checkOrigin("http://localhost:5173"));
        assertEquals("http://localhost:8088", config.checkOrigin("http://localhost:8088"));
        assertEquals("https://demo.trycloudflare.com",
                config.checkOrigin("https://demo.trycloudflare.com"));
        assertEquals(null, config.checkOrigin("https://temporary-tunnel.invalid"));
        assertTrue(config.getAllowedMethods().contains("OPTIONS"));
        assertEquals(Boolean.TRUE, config.getAllowCredentials());
    }
}
