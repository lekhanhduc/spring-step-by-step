package vn.khanhduc.courseservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.entity.User;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateAccessToken(User user) {
        // 1. Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // 2. Payload
        Date issueTime = new Date();

        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();// [USER, ADMIN, STAFF]

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(new Date(issueTime.toInstant().plus(30, ChronoUnit.MINUTES).toEpochMilli()))
                .claim("id", user.getId())
                .claim("authorities", authorities)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        // 3. Chữ kí
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) {
        // 1. Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // 2. Payload
        Date issueTime = new Date();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(new Date(issueTime.toInstant().plus(14, ChronoUnit.DAYS).toEpochMilli()))
                .claim("id", user.getId())
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        // 3. Chữ kí
        JWSObject jwsObject = new  JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwsObject.serialize();
    }

}
