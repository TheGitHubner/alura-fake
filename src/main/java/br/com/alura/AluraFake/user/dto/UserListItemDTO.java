package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.model.User;
import lombok.Getter;

import java.io.Serializable;

public class UserListItemDTO implements Serializable {

    @Getter
    private String name;

    @Getter
    private String email;

    @Getter
    private Role role;

    public UserListItemDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
