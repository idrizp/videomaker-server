package dev.idriz.videomaker.controller.authentication;

import dev.idriz.videomaker.service.AuthService;
import dev.idriz.videomaker.token.JWT;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/")
public class AuthController {

    private final AuthService authService;
    private final JWT jwt;

    public AuthController(AuthService authService, JWT jwt) {
        this.authService = authService;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        try {
            var user = authService.createUser(
                    request.username(),
                    request.password(),
                    request.email()
            );
            return ResponseEntity.ok(new AuthResponse(jwt.createToken(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("User already exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        var user = authService.login(request.usernameOrEmail(), request.password());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        return ResponseEntity.ok(new AuthResponse(jwt.createToken(user)));
    }

    public record AuthResponse(String token) {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 64) String username,
            @NotBlank @Size(min = 8, max = 64) String password,
            @NotBlank @Email String email
    ) {
    }

    public record LoginRequest(
            @NotBlank @Size(min = 3) String usernameOrEmail,
            @NotBlank @Size(min = 8, max = 64) String password
    ) {
    }
}
