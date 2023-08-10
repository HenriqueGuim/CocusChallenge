package pt.cocus.cocuschallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.cocus.cocuschallenge.model.ListOfRepos;
import pt.cocus.cocuschallenge.model.RepoModel;
import pt.cocus.cocuschallenge.model.UsernameModel;
import pt.cocus.cocuschallenge.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newCachedThreadPool;

@Service
@Slf4j
public class UserReposService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GITHUB_API_URL_REPOS = "https://api.github.com/users/{username}/repos";
    private final ExecutorService executorService = newCachedThreadPool();


    public ListOfRepos getUserRepos(@NotNull UsernameModel userName) throws URISyntaxException, JsonProcessingException, InterruptedException {
        ListOfRepos listOfRepos = new ListOfRepos(userName.username());
        Queue<Future<?>> listOfFutures = new LinkedList<>();

        log.info("Getting repos for user: " + userName.username());
        int page = 1;
        while (true) {
            JsonNode repos = getReposByPage(userName.username(), page);
            page++;

            if (repos.isEmpty()) {
                break;
            }

            repos.forEach(repo -> handleRepo(repo, listOfRepos));
        }
/*
        while (!listOfFutures.isEmpty()) {
            Future<?> future = listOfFutures.poll();
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

 */

        return listOfRepos;
    }

    private void handleRepo(JsonNode repo, ListOfRepos listOfRepos) {
        if (repo.get("fork").asBoolean()) {
            return;
        }

        RepoModel repoModel = null;
        try {
            repoModel = new RepoModel(
                    repo.get("name").asText(),
                    repo.get("owner").get("login").asText()
            );
        } catch (URISyntaxException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        listOfRepos.addRepo(repoModel);
        log.info("Repo: " + repo.get("name").asText());
    }

    private JsonNode getReposByPage(String username, int pageNumber) throws URISyntaxException, JsonProcessingException {

        URI uri = new URI(String.format("%s?page=%d&sort=created",
                this.GITHUB_API_URL_REPOS.replace("{username}", username),
                pageNumber));

        ResponseEntity<String> response = Utils.apiGetCallToGithub(uri, restTemplate);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.getBody());
    }
}
