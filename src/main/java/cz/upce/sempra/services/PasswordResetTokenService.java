package cz.upce.sempra.services;

import cz.upce.sempra.domains.PasswordResetToken;
import cz.upce.sempra.repositories.IPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PasswordResetTokenService {
    private final IPasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken createPasswordResetToken(final PasswordResetToken passwordResetToken) {
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken getPasswordResetToken(final String token) throws ResourceNotFoundException {
        PasswordResetToken result = passwordResetTokenRepository.findByToken(token);

        if (result == null) throw new ResourceNotFoundException();

        return result;
    }

    public PasswordResetToken getPasswordResetTokenByUserId(final Long id) {
        return passwordResetTokenRepository.findByUserId(id);
    }

    public void removePasswordResetToken(final PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}