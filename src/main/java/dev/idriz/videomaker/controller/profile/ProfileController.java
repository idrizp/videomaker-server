package dev.idriz.videomaker.controller.profile;

import dev.idriz.videomaker.entity.AppUser;
import dev.idriz.videomaker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

import static dev.idriz.videomaker.service.AuthService.getAuthenticatedUser;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    public record ProfileResponseDTO(
            String id,
            BigInteger balance,
            String role,
            String username,
            String email,
            String firstName,
            String lastName
    ) { }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        AppUser user = getAuthenticatedUser();
        return ResponseEntity.ok(new ProfileResponseDTO(
                user.getId().toString(),
                user.getBalance(),
                user.getRole(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        ));
    }

}
