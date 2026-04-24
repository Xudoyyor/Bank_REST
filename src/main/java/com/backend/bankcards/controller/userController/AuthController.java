package com.backend.bankcards.controller.userController;
import com.backend.bankcards.entity.UserEntity;
import com.backend.bankcards.repository.UserRepository;
import com.backend.bankcards.service.securityService.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/register")
    public String register(@RequestBody UserEntity user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    @PostMapping("/login")
    public String login(@RequestBody UserEntity request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity user = repo.findByUsername(request.getUsername()).orElseThrow();

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }
}