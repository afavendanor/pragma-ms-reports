package co.com.pragma.api.security.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public final class TokenJwtConfig {

    private TokenJwtConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final String SECRET = "miSuperClaveSecretaSeguraDe32CharsMinimo123";
    public static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    public static final String PREFIX_TOKEN = "Bearer ";

}
