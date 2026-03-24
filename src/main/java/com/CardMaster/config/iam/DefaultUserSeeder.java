package com.CardMaster.config.iam;

import com.CardMaster.Enum.iam.UserEnum;
import com.CardMaster.dao.iam.UserRepository;
import com.CardMaster.model.iam.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class DefaultUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createDefaultUserIfMissing(
                "Customer Demo",
                "customer@test.com",
                "9876543210",
                "Customer@123",
                UserEnum.CUSTOMER
        );
    }

    private void createDefaultUserIfMissing(
            String name,
            String email,
            String phone,
            String rawPassword,
            UserEnum role
    ) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        userRepository.save(user);
    }
}
