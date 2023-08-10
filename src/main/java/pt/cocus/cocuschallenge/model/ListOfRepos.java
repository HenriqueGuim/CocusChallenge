package pt.cocus.cocuschallenge.model;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ListOfRepos {

    private final String username;
    private CopyOnWriteArrayList<RepoModel> repos;
    public ListOfRepos(String username) {
        this.username = username;
        this.repos = new CopyOnWriteArrayList<>();
    }

    public void addRepo(RepoModel repoModel) {
        this.repos.add(repoModel);
    }

    public void removeRepo(RepoModel repoModel) {
        repos.remove(repoModel);
    }
}
