package cz.upce.sempra.controllers;

import cz.upce.sempra.domains.EmailMessage;
import cz.upce.sempra.domains.PasswordResetToken;
import cz.upce.sempra.domains.User;
import cz.upce.sempra.dtos.UserChangePasswordDto;
import cz.upce.sempra.dtos.UserInputDto;
import cz.upce.sempra.dtos.UserNewPasswordDto;
import cz.upce.sempra.dtos.UserPasswordResetDto;
import cz.upce.sempra.services.*;
import cz.upce.sempra.utils.JwtTokenUtil;
import cz.upce.sempra.utils.PasswordResetTokenGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "Authorization")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    protected final Log logger = LogFactory.getLog(getClass());
    @Autowired
    UserService userService;
    @Autowired
    PasswordResetTokenService passwordResetTokenService;
    @Autowired
    EmailService emailService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "Login.")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Validated @RequestBody final UserInputDto userInputDto) {
        String password = userInputDto.getPassword();
        String username = userInputDto.getUsername();
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            if (auth.isAuthenticated()) {
                logger.info("Logged In.");
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                String token = jwtTokenUtil.generateToken(userDetails);
                responseMap.put("message", "Logged In.");
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("message", "Invalid Credentials.");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("message", "User is disabled.");
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("message", "Invalid Credentials.");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("message", "Something went wrong.");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @ApiOperation(value = "Register.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody final UserInputDto userInputDto) throws ResourceNotFoundException {
        User existingUser = userService.getUserByUsername(userInputDto.getUsername());

        if (existingUser != null) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "User already exists.");
            return ResponseEntity.status(409).body(responseMap);
        }

        var user = toEntity(userInputDto);

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.createUser(user);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Account created successfully.");
        responseMap.put("user", user);
        return ResponseEntity.ok(responseMap);
    }

    @ApiOperation(value = "Reset password.")
    @PostMapping("/reset")
    public ResponseEntity<?> passwordReset(@Validated @RequestBody final UserPasswordResetDto userPasswordResetDto) {
        try {
            User user = userService.getUserByUsername(userPasswordResetDto.getUsername());

            if (user == null) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "User not found.");
                return ResponseEntity.status(404).body(responseMap);
            }

            PasswordResetToken oldPasswordResetToken = passwordResetTokenService.getPasswordResetTokenByUserId(user.getId());

            if (oldPasswordResetToken != null) {
                passwordResetTokenService.removePasswordResetToken(oldPasswordResetToken);
            }

            String resetToken = PasswordResetTokenGenerator.generateResetToken();

            passwordResetTokenService.createPasswordResetToken(
                    new PasswordResetToken(
                            resetToken,
                            user,
                            LocalDateTime.now().plusMinutes(30)
                    )
            );

            emailService.sendEmail(
                    new EmailMessage(
                            "",
                            "nnpdatestupce@gmail.com",
                            "Password reset token.",
                            resetToken
                    )
            );

            return ResponseEntity.ok("Password reset token sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send password reset token.");
        }
    }

    @ApiOperation(value = "Set new password.")
    @PostMapping("/new_password")
    public ResponseEntity<?> setNewPassword(@Validated @RequestBody final UserNewPasswordDto userNewPasswordDto) {
        String newPassword = userNewPasswordDto.getNewPassword();
        String resetToken = userNewPasswordDto.getResetToken();

        try {
            PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(resetToken);

            if (passwordResetToken == null || passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(401).body("Invalid or expired reset token.");
            }

            User user = passwordResetToken.getUser();

            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userService.updateUser(user);

            passwordResetTokenService.removePasswordResetToken(passwordResetToken);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "New password set successfully.");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Failed to set a new password.");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @ApiOperation(value = "Change password.")
    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(@Validated @RequestBody final UserChangePasswordDto userChangePasswordDto) {
        String newPassword = userChangePasswordDto.getNewPassword();
        String oldPassword = userChangePasswordDto.getOldPassword();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.getUserByUsername(authentication.getName());

            if (user == null || !new BCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid old password.");
            }

            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userService.updateUser(user);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Password changed successfully.");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "Failed to change the password.");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    private User toEntity(final UserInputDto dto) {
        return new User(
                dto.getUsername(),
                dto.getPassword()
        );
    }
}