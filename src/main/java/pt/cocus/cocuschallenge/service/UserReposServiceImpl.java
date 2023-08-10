package pt.cocus.cocuschallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.cocus.cocuschallenge.model.User;
import pt.cocus.cocuschallenge.model.RepoModel;
import pt.cocus.cocuschallenge.model.UsernameModel;
import pt.cocus.cocuschallenge.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newCachedThreadPool;

@Service
@Slf4j
public class UserReposServiceImpl {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = newCachedThreadPool();


    public User getUserRepos(@NotNull UsernameModel userName) throws URISyntaxException, JsonProcessingException, InterruptedException, ExecutionException {
        User user = new User(userName.username());
        Queue<Future<?>> listOfFutures = new LinkedList<>();

        log.info("Getting repos for user: " + userName.username());
        int page = 1;
        while (true) {
            JsonNode repos = getReposByPage(userName.username(), page);
            page++;

            if (repos.isEmpty()) {
                break;
            }

            repos.forEach(repo -> {
                listOfFutures
                        .add(executorService.submit(
                                new HandleRepo(repo, user)
                        )
                );
            });
        }

        while (!listOfFutures.isEmpty()) {
            Future<?> future = listOfFutures.poll();

            future.get();

        }

        return user;
    }

    public JsonNode getReposByPage(String username, int pageNumber) throws URISyntaxException, JsonProcessingException {

        String GITHUB_API_URL_REPOS = "https://api.github.com/users/{username}/repos";
        URI uri = new URI(String.format("%s?page=%d",
                GITHUB_API_URL_REPOS.replace("{username}", username),
                pageNumber));
        ResponseEntity<String> response;

        response = Utils.apiGetCallToGithub(uri, restTemplate);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.getBody());
    }

    public static class HandleRepo implements Runnable {
        private final JsonNode repo;
        private final User user;

        public HandleRepo(JsonNode repo, User user) {
            this.repo = repo;
            this.user = user;
        }

        @SneakyThrows
        @Override public void run() {
            RepoModel repoModel = new RepoModel(repo.get("name").asText(), user.getUsername());
            user.addRepo(repoModel);
        }
    }



}
