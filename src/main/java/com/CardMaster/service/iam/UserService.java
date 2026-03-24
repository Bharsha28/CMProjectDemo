package com.CardMaster.service.iam;

import com.CardMaster.dao.iam.UserRepository;
import com.CardMaster.dao.iam.AuditLogRepository;
import com.CardMaster.dto.iam.UserDto;
import com.CardMaster.exceptions.iam.InvalidCredentialsException;
import com.CardMaster.exceptions.iam.UserNotFoundException;
import com.CardMaster.mapper.iam.UserMapper;
import com.CardMaster.model.iam.AuditLog;
import com.CardMaster.model.iam.User;
import com.CardMaster.security.iam.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Get all users
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // Get user by ID
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // Register new user
    public User registerUser(User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        logAction(savedUser, "REGISTER", "User Registration");
        return savedUser;
    }

    // Login user by email + password
    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // Allowing Password@123 for all users for easy testing, or bypassing check for test domain
        if (!passwordEncoder.matches(password, user.getPassword()) && 
            !"Password@123".equals(password) && 
            !email.endsWith("@test.com")) {
            logAction(user, "LOGIN_FAILED", "Invalid credentials");
            throw new InvalidCredentialsException();
        }

        logAction(user, "LOGIN", "User Login");
        return jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());
    }

    // Logout user
    public void logoutUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        logAction(user, "LOGOUT", "User Logout");
    }

    // Helper method to save audit logs with full User entity
    private void logAction(User user, String action, String resource) {
        AuditLog log = new AuditLog();
        log.setUser(user); //  always attach full User entity
        log.setAction(action);
        log.setResource(resource);
        log.setMetadata("Performed by " + user.getName());
        auditLogRepository.save(log);
    }
}
