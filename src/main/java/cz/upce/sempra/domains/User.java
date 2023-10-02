package cz.upce.sempra.domains;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "username", unique = true)
    @NotBlank
    @NotNull
    @Size(max = 256)
    private String username;

    @Column(name = "password")
    @NotBlank
    @NotNull
    @Size(max = 256)
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}