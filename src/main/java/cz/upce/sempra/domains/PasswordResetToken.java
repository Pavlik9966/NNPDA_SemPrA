package cz.upce.sempra.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Column(name = "password_reset_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "token", unique = true)
    @NotBlank
    @NotNull
    @Size(max = 512)
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date")
    @NotNull
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }
}