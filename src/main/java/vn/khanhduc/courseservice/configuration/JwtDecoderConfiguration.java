package vn.khanhduc.courseservice.configuration;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import vn.khanhduc.courseservice.entity.Token;
import vn.khanhduc.courseservice.repository.RedisTokenRepository;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtDecoderConfiguration implements JwtDecoder {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final RedisTokenRepository redisTokenRepository;
    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Trích xuất thông tin từ token
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if(expirationTime.before(new Date())) {
                log.info("Token expired");
                throw new JwtException("Token expired");
            }

            // Kiểm tra token đã bị logout chưa ?
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            Optional<Token> tokenOptional = redisTokenRepository.findById(jwtId);
            if(tokenOptional.isPresent()) {
                // nếu tồn tại --> đã logout
                log.info("Token is logged");
                throw new RuntimeException("Token is logged");
            }

            // decode, verify token
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HS512");
            if(nimbusJwtDecoder == null) {
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }
            return nimbusJwtDecoder.decode(token);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
