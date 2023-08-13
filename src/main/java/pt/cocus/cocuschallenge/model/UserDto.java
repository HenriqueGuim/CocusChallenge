package pt.cocus.cocuschallenge.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

public record UserDto(@Validated @NotNull String username) {
    public UserDto(@NotNull String username) {

        this.username = username;
    }
}


