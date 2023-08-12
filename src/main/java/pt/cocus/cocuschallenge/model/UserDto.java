package pt.cocus.cocuschallenge.model;

import org.jetbrains.annotations.NotNull;

public record UserDto(@NotNull String username) {
    public UserDto(@NotNull String username) {

        this.username = username;
    }
}


