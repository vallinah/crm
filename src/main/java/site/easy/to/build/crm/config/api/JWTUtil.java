package site.easy.to.build.crm.config.api;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    private final String jwtSecret = "ac42b8d507e71b1d4d401af6299f30fb428e93aa238aa791cb8dc272ca23b1c1d1403e6b790029bb488bd10a2a999377837abdf26754da0615cae269c55b49d158e5d17b30c0c24f1e76b71f8d3dd7cec7cc680aae856169c010b5c78568183c30471991c31ad2cb2d4cf73e0a9e0c69cb7aa21162410bc8ec26c7b7e1c7487555952c28a7c36ca3300bc1d006457416a5d3c7450faff06d89e48b60b6bf2f9b563074b96d4c66cbede2ce0fdf37bb6da7921b9ecd43942a3f4ab96fc0023ec875eeeaa1cfb012dc264d58f8ea698d1043d7842879b18f6561526d1b2cd8427f94e466308f6b7b42ddb4879df3b91b53edd7e40aa374f990389dc455d7c53ffa"; // >256 bits// à stocker dans les properties/env
    private final long jwtExpirationMs = 86400000; // 1 jour

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
