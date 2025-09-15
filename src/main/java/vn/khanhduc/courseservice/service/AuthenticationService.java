package vn.khanhduc.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.dto.request.LoginRequest;
import vn.khanhduc.courseservice.dto.response.LoginResponse;
import vn.khanhduc.courseservice.entity.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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

}
