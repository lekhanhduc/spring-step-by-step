package vn.khanhduc.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.dto.request.CreateUserRequest;
import vn.khanhduc.courseservice.dto.response.CreateUserResponse;
import vn.khanhduc.courseservice.entity.User;
import vn.khanhduc.courseservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        // 1. Kiểm tra email có tồn tại trong db
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());

        userRepository.save(user);

        return CreateUserResponse.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
    }

}
