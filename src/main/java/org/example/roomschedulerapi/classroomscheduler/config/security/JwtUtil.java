package org.example.roomschedulerapi.classroomscheduler.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.roomschedulerapi.classroomscheduler.model.Admin;       // ✅ Import your Admin entity
import org.example.roomschedulerapi.classroomscheduler.model.Instructor; // ✅ Import your Instructor entity
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.ms}")
    private long jwtExpiration;

    @Value("${jwt.password.reset.expiration.ms}")
    private long passwordResetExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // 1. Add roles to the token (existing logic)
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        extraClaims.put("roles", roles);

        // 2. ✅ ADD FIRST NAME AND LAST NAME TO THE TOKEN
        // Check the type of the user to get the correct name fields
        if (userDetails instanceof Instructor) {
            Instructor instructor = (Instructor) userDetails;
            extraClaims.put("firstName", instructor.getFirstName());
            extraClaims.put("lastName", instructor.getLastName());
            extraClaims.put("instructorId", instructor.getInstructorId());
        } else if (userDetails instanceof Admin) {
            Admin admin = (Admin) userDetails;
            // Assuming your Admin entity has a 'fullName' field
            // If it has firstName/lastName, use those instead for consistency
            extraClaims.put("firstName", admin.getFullName());
            extraClaims.put("lastName", ""); // Admins might not have a separate last name
        }

        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ CORRECTED to use the modern parserBuilder() method
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // You can also add specific extractors for the new claims if needed
    public String extractFirstName(String token) {
        return extractClaim(token, claims -> claims.get("firstName", String.class));
    }

    public String generatePasswordResetToken(UserDetails userDetails) {
        // This token is specifically for resetting the password and has a shorter lifespan
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + passwordResetExpiration)) // Use short expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}