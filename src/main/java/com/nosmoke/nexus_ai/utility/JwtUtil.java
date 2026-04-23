package com.nosmoke.nexus_ai.utility;

import java.security.Key;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * JwtUtil is a utility class responsible for managing JSON Web Tokens (JWT) in the application.
 * It handles token generation, parsing, and validation using the JJWT library.
 * JWTs are used for stateless authentication - the token itself contains user information
 * and a cryptographic signature, so the server doesn't need to store session data.
 */
@Component
public class JwtUtil {

    // The JWT secret key string loaded from application configuration (application.properties or application.yml)
    // This is a sensitive value and should be kept secure in production environments
    @Value("${jwt.secret}")
    private String key;

    // The cryptographic key object used for signing and verifying JWT tokens
    // This is initialized in the @PostConstruct method below
    private Key secretKey;

    // The token expiration time in milliseconds loaded from application configuration
    // This determines how long a generated JWT token remains valid before it expires
    @Value("${jwt.expiration}")
    private Long expirationTime;

    /**
     * Generates a new JWT token for an authenticated user.
     * 
     * This method:
     * 1. Takes a UserDetails object (representing an authenticated user from Spring Security)
     * 2. Creates a new JWT token builder
     * 3. Sets the token subject to the username (this identifies who the token belongs to)
     * 4. Sets the "issued at" timestamp (when the token was created)
     * 5. Sets the expiration time (when the token becomes invalid)
     * 6. Signs the token using the secret key (ensures the token hasn't been tampered with)
     * 7. Compacts it into a URL-safe string that can be sent to clients
     * 
     * @param userDetails The authenticated user for whom to generate the token
     * @return A signed JWT token as a String that can be sent to the client
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Converts a Base64-encoded string key into a cryptographic Key object.
     * 
     * This method decodes the Base64 string and creates an HMAC-SHA key suitable for
     * signing and verifying JWT tokens. HMAC (Hash-based Message Authentication Code)
     * is a symmetric algorithm where the same secret key is used for both signing and verification.
     * 
     * @param key The Base64-encoded secret key string from configuration
     * @return A Key object that can be used for JWT operations
     */
    public Key convertKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    /**
     * Extracts the username from a JWT token.
     * 
     * This method:
     * 1. Creates a JWT parser with the secret key (to verify the token signature)
     * 2. Parses the token and verifies its signature (ensures it hasn't been tampered with)
     * 3. Gets the token body/claims (the decoded payload containing user information)
     * 4. Extracts and returns the subject (the username that was embedded when the token was created)
     * 
     * @param token The JWT token string to parse
     * @return The username contained in the token
     * @throws Exception if the token is invalid, expired, or the signature doesn't match
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the expiration date from a JWT token.
     * 
     * This method parses the token and retrieves the expiration timestamp.
     * The expiration date determines when the token becomes invalid.
     * 
     * @param token The JWT token string to parse
     * @return The Date when this token will expire
     * @throws Exception if the token is invalid or the signature doesn't match
     */
    public Date extractExpirationDate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    /**
     * Validates that a JWT token is still valid and belongs to the given user.
     * 
     * This method performs two checks:
     * 1. Verifies that the username in the token matches the authenticated user's username
     * 2. Checks that the token hasn't expired (expiration date is after the current time)
     * 
     * Both conditions must be true for the token to be considered valid.
     * 
     * @param token The JWT token to validate
     * @param userDetails The authenticated user to check against
     * @return true if the token belongs to the user and hasn't expired, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        // Extract the username from the token
        String check = extractUsername(token);
        // Get the current time to compare against token expiration
        Date date = new Date(System.currentTimeMillis());

        // Check if username matches AND token hasn't expired
        return (check.equals(userDetails.getUsername()) && (extractExpirationDate(token).after(date)));
    }

    /**
     * Initializes the secret key after the Spring bean is constructed.
     * 
     * @PostConstruct is a Spring annotation that marks a method to run after:
     * 1. The bean has been instantiated (constructor called)
     * 2. Dependency injection is complete (all @Value fields are populated)
     * 
     * This method converts the Base64-encoded key string (from configuration) into
     * a cryptographic Key object that's ready for JWT signing and verification operations.
     * We do this in @PostConstruct rather than in the constructor because we need to
     * wait for the @Value annotations to inject the key string first.
     */
    @PostConstruct
    public void KeyConstructor() {
        this.secretKey = convertKey(key);
    }

}
