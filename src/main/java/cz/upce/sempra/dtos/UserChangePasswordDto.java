package cz.upce.sempra.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserChangePasswordDto {
    @NotBlank
    @NotNull
    @Size(max = 256)
    private String newPassword;

    @NotBlank
    @NotNull
    @Size(max = 256)
    private String oldPassword;
}