package pt.cocus.cocuschallenge.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pt.cocus.cocuschallenge.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;


public class RepoModel {
    public String name;
    public List<Branch> branches;
    private final String GITHUB_API_URL_BRANCHS = "https://api.github.com/repos/{username}/{repo}/branches";
    private final RestTemplate restTemplate = new RestTemplate();

    private final ExecutorService executorService = newCachedThreadPool();

    public RepoModel(String repoName, String userName) throws URISyntaxException, JsonProcessingException {
        this.name = repoName;
        this.branches = new ArrayList<>();
        getBranches(userName);
    }

    private void getBranches(String username) throws URISyntaxException, JsonProcessingException {

        int page = 1;
        while (true) {
            JsonNode repos = getBranchesFromGit(username, page);
            page++;

            if (repos.isEmpty()) {
                break;
            }

            repos.forEach(this::handleBranch);
        }
    }

    private void handleBranch(JsonNode repo) {
        Branch branch = new Branch(repo.get("name").asText(),
                repo.get("commit")
                        .get("sha").asText());

        this.branches.add(branch);
    }

    public JsonNode getBranchesFromGit(String username, int pageNumber) throws URISyntaxException, JsonProcessingException {

        URI uri = new URI(String.format("%s?page=%d&sort=created",
                this.GITHUB_API_URL_BRANCHS
                        .replace("{username}", username)
                        .replace("{repo}", this.name),
                pageNumber)
        );

        ResponseEntity<String> response = Utils.apiGetCallToGithub(uri, restTemplate);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readTree(response.getBody());
    }
}
