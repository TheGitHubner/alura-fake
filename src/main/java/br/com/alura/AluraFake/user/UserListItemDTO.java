package br.com.alura.AluraFake.user;

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
