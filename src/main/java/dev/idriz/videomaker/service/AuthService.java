package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, AppUserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public boolean isUserEnabled(String username) {
        return userRepository.findByUsernameIgnoreCase(username).map(AppUser::isEnabled).orElse(false);
    }

    public boolean isUserAdmin(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(user -> user.getRole().equalsIgnoreCase("ADMIN")).orElse(false);
    }

    public boolean isUser(String username) {
        return userRepository.findByUsernameIgnoreCase(username).isPresent();
    }

    public AppUser createUser(
            String username,
            String password,
            String email,
            String phoneNumber
    ) {
        AppUser user = new AppUser();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        user.setBalance(BigInteger.ZERO);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setVideos(new ArrayList<>());
        user.setRole("USER");

        return userRepository.save(user);
    }

    public AppUser login(String username, String password) {
        Optional<AppUser> user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            logger.info("User not found: {}", username);
            return null;
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            logger.info("Invalid password for user: {}", username);
            return null;
        }
        return user.get();
    }

}
