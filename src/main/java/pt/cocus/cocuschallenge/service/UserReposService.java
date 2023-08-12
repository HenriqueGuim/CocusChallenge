package pt.cocus.cocuschallenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import pt.cocus.cocuschallenge.model.User;
import pt.cocus.cocuschallenge.model.UserDto;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public interface UserReposService {
    User getUserRepos(@NotNull UserDto userName) throws URISyntaxException, JsonProcessingException, InterruptedException, ExecutionException;

    JsonNode getReposByPage(String username, int pageNumber) throws URISyntaxException, JsonProcessingException;

}
