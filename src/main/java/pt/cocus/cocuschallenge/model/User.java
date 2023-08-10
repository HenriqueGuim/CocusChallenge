package pt.cocus.cocuschallenge.model;

import lombok.Getter;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class User {

    private final String username;
    private CopyOnWriteArrayList<RepoModel> repos;
    public User(String username) {
        this.username = username;
        this.repos = new CopyOnWriteArrayList<>();
    }

    public void addRepo(RepoModel repoModel) {
        this.repos.add(repoModel);
    }
}
