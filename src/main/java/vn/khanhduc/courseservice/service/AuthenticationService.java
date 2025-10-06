package vn.khanhduc.courseservice.service;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.dto.request.LoginRequest;
import vn.khanhduc.courseservice.dto.response.LoginResponse;
import vn.khanhduc.courseservice.entity.Token;
import vn.khanhduc.courseservice.entity.User;
import vn.khanhduc.courseservice.repository.RedisTokenRepository;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTokenRepository redisTokenRepository;

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authenticate = authenticationManager.authenticate(authenticationRequest);

        User user = (User) authenticate.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String accessToken) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

        Long expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();
        Long now = new Date().getTime();

        Token token = Token.builder()
                .tokenId(jwtId)
                .expiredTime(expiredTime - now)
                .build();

        redisTokenRepository.save(token);
    }

}
