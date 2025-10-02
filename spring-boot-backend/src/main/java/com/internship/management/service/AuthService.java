package com.internship.management.service;

import com.internship.management.dto.AuthDTO;
import com.internship.management.dto.UserDTO;
import com.internship.management.entity.User;
import com.internship.management.repository.UserRepository;
import com.internship.management.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthDTO.CheckEmailResponse checkEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return AuthDTO.CheckEmailResponse.builder()
                    .exists(false)
                    .hasPassword(false)
                    .message("Email non trouvé dans le système")
                    .build();
        }

        boolean hasPassword = user.getPassword() != null && !user.getPassword().isEmpty();

        return AuthDTO.CheckEmailResponse.builder()
                .exists(true)
                .hasPassword(hasPassword)
                .message(hasPassword ?
                        "Compte déjà activé. Veuillez vous connecter." :
                        "Email trouvé. Créez votre mot de passe.")
                .build();
    }

    @Transactional
    public AuthDTO.AuthResponse createPassword(AuthDTO.CreatePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            throw new RuntimeException("Le compte est déjà activé");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .user(UserDTO.fromEntity(user))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Compte non activé. Veuillez créer votre mot de passe.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new RuntimeException("Compte désactivé. Contactez l'administrateur.");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        return AuthDTO.AuthResponse.builder()
                .token(token)
                .user(UserDTO.fromEntity(user))
                .build();
    }
}
