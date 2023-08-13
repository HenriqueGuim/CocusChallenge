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
import pt.cocus.cocuschallenge.model.RepoModel;
import pt.cocus.cocuschallenge.model.User;
import pt.cocus.cocuschallenge.model.UserDto;
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
public class UserReposServiceImpl implements UserReposService {
    public final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = newCachedThreadPool();


    @Override
    public User getUserRepos(@NotNull UserDto userName) throws URISyntaxException, JsonProcessingException, InterruptedException, ExecutionException {
        User user = new User(userName.username());
        Queue<Future<?>> listOfFutures = new LinkedList<>();

        log.info("Getting repos for user: " + userName.username());
        int page = 1;
        while (true) {
            log.info("Getting repos for user: " + userName.username() + " page: " + page);
            JsonNode repos = getReposByPage(userName.username(), page);
            page++;

            if (repos.isEmpty()) {
                break;
            }

            repos.forEach(repo -> listOfFutures
                    .add(executorService.submit(
                            new HandleRepo(repo, user)
                    )
            ));
        }
        log.info(String.format("Waiting for all threads of username: %s to finish", userName.username()));
        while (!listOfFutures.isEmpty()) {
            Future<?> future = listOfFutures.poll();

            future.get();

        }

        return user;
    }

    @Override
    public JsonNode getReposByPage(String username, int pageNumber) throws URISyntaxException, JsonProcessingException {
        String GITHUB_API_URL_REPOS = "https://api.github.com/users/{username}/repos";
        URI uri = new URI(String.format("%s?page=%d",
                GITHUB_API_URL_REPOS.replace("{username}", username),
                pageNumber));

        ResponseEntity<String> response = Utils.apiGetCallToGithub(uri, restTemplate);

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

        public RepoModel createRepoModel(String repoName) {
            return new RepoModel(repoName);
        }

        @SneakyThrows
        @Override
        public void run() {
            if (repo.get("fork").asBoolean()) {
                return;
            }
            RepoModel repoModel = createRepoModel(repo.get("name").asText());
            repoModel.retrieveBranchesForRepo(user.getUsername());
            user.addRepo(repoModel);
        }
    }
}
