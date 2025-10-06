package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
public class NewUserDTO {

    @NotNull
    @Length(min = 3, max = 50)
    @Getter
    @Setter
    private String name;

    @NotBlank
    @Email
    @Getter
    @Setter
    private String email;

    @NotNull
    @Getter
    @Setter
    private Role role;

    @Pattern(regexp = "^$|^.{6}$", message = "Password must be exactly 6 characters long if provided")
    private String password;

    public User toModel() {
        return new User(name, email, role);
    }

}
