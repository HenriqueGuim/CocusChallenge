package pt.cocus.cocuschallenge.model;

import lombok.Data;
import lombok.NonNull;

public record UsernameModel(String username) {
    public UsernameModel(String username) {
        this.username = username;
    }
}


