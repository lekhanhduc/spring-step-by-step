package vn.khanhduc.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.khanhduc.courseservice.common.UserStatus;
import vn.khanhduc.courseservice.common.UserType;
import vn.khanhduc.courseservice.dto.request.CreateUserRequest;
import vn.khanhduc.courseservice.dto.response.CreateUserResponse;
import vn.khanhduc.courseservice.entity.Role;
import vn.khanhduc.courseservice.entity.User;
import vn.khanhduc.courseservice.entity.UserHasRole;
import vn.khanhduc.courseservice.repository.RoleRepository;
import vn.khanhduc.courseservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static vn.khanhduc.courseservice.common.UserType.USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setStatus(UserStatus.ACTIVE);

        Role role = roleRepository.findByName(USER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(USER)
                        .build()));
        user.addRole(role);
        userRepository.save(user);

        return CreateUserResponse.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
    }

    @PreAuthorize("hasAuthority('USER')") // SCOPE_USER
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
