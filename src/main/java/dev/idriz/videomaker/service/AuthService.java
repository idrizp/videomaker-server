package dev.idriz.videomaker.service;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
            String firstName,
            String lastName
    ) {
        if (userRepository.findByUsernameEqualsOrEmailEqualsIgnoreCase(username, email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        AppUser user = new AppUser();

        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setBalance(BigInteger.ZERO);
        user.setEmail(email);
        user.setVideos(new ArrayList<>());
        user.setRole("USER");

        return userRepository.save(user);
    }

    public AppUser login(String usernameOrEmail, String password) {
        Optional<AppUser> user = userRepository.findByUsernameEqualsOrEmailEqualsIgnoreCase(usernameOrEmail, usernameOrEmail);
        if (user.isEmpty()) {
            logger.info("User not found: {}", usernameOrEmail);
            return null;
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            logger.info("Invalid password for user: {}", usernameOrEmail);
            return null;
        }
        return user.get();
    }

    /**
     * Authenticates the current thread as the given user
     *
     * @param userId the user id
     * @throws RuntimeException if the user is not found or is not enabled
     */
    public void authenticateCurrentThread(UUID userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.isEnabled()) {
            throw new RuntimeException("User is not enabled");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), Set.of(new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().toUpperCase()
        )));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static AppUser getAuthenticatedUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
