package vn.khanhduc.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.dto.request.LoginRequest;
import vn.khanhduc.courseservice.dto.response.LoginResponse;
import vn.khanhduc.courseservice.entity.User;
import vn.khanhduc.courseservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String passwordHash = user.getPassword();
        PasswordEncoder  passwordEncoder = new BCryptPasswordEncoder();

        if(!passwordEncoder.matches(password, passwordHash)){
            throw new RuntimeException("Incorrect password");
        }

        // tạo token
        return LoginResponse.builder()
                .token("Token123456")
                .build();
    }

}
