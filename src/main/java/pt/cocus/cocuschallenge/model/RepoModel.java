package pt.cocus.cocuschallenge.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pt.cocus.cocuschallenge.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newCachedThreadPool;

@Slf4j
@NoArgsConstructor
public class RepoModel {
    public String name;
    public List<Branch> branches = new CopyOnWriteArrayList<>();
    public transient final RestTemplate restTemplate = new RestTemplate();

    private final ExecutorService executorService = newCachedThreadPool();

    public RepoModel(String repoName) {
        this.name = repoName;
    }

    public void retrieveBranchesForRepo(String username) throws URISyntaxException, JsonProcessingException, ExecutionException, InterruptedException {
        Queue<Future<?>> listOfFutures = new LinkedList<>();

        int page = 1;
        while (true) {
            log.info("Getting branches for repo: " + this.name + " page: " + page);

            JsonNode branches = getBranchesFromGit(username, page);
            page++;

            if (branches.isEmpty()) {
                break;
            }

            branches.forEach(branch -> listOfFutures
                    .add(executorService.submit(
                            new HandleBranches(branch, this.branches)
            )));
        }

        log.info(String.format("Waiting for all branch of repo %s threads to finish", this.name));
        while (!listOfFutures.isEmpty()) {
            Future<?> future = listOfFutures.poll();

            future.get();

        }
    }

    private JsonNode getBranchesFromGit(String username, int pageNumber) throws URISyntaxException, JsonProcessingException {

        String GITHUB_API_URL_BRANCHS = "https://api.github.com/repos/{username}/{repo}/branches";
        URI uri = new URI(String.format("%s?page=%d",
                GITHUB_API_URL_BRANCHS
                        .replace("{username}", username)
                        .replace("{repo}", this.name),
                pageNumber)
        );

        ResponseEntity<String> response = Utils.apiGetCallToGithub(uri, restTemplate);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.getBody());
    }

    private static class HandleBranches implements Runnable {
        private final JsonNode branch;
        private final List<Branch> branches;

        private HandleBranches(JsonNode repo, List<Branch> branches) {
            this.branch = repo;
            this.branches = branches;
        }

        @Override
        public void run() {
            Branch branch = new Branch(
                    this.branch.get("name").asText(),
                    this.branch.get("commit")
                            .get("sha").asText());

            this.branches.add(branch);
        }
    }
}
