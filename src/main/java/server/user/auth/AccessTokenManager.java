package server.user.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class AccessTokenManager {

    private static final String CLAIM_LOGIN = "login";

    private final Key signingKey;

    public AccessTokenManager(Key signingKey) {
        this.signingKey = signingKey;
    }

    public AccessToken decodeFromJws(String jws) {
        String login = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(jws)
                .getBody()
                .get(CLAIM_LOGIN, String.class);
        return new AccessToken(login);
    }

    public String encodeToJws(AccessToken accessToken) {
        if (!accessToken.isValid()) {
            throw new IllegalStateException("Trying to get value for invalid token");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_LOGIN, accessToken.getLogin());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(signingKey)
                .compact();
    }

    public static AccessTokenManager withBase64EncodedKey(String key) {
        return new AccessTokenManager(Keys.hmacShaKeyFor(Decoders.BASE64.decode(key)));
    }
}
