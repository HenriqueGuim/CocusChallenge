package pt.cocus.cocuschallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import pt.cocus.cocuschallenge.model.RepoModel;
import pt.cocus.cocuschallenge.model.User;
import pt.cocus.cocuschallenge.model.UserDto;
import pt.cocus.cocuschallenge.service.UserReposServiceImpl;
import pt.cocus.cocuschallenge.client.GitHubClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserRepoServiceImplTests {

    UserReposServiceImpl userReposService= spy(UserReposServiceImpl.class);

    @Test
    void testGetUserRepos() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {

        UserDto userDto = new UserDto("TestUserName");
        User expectedUser = new User(userDto.username());
        JsonNode firstPageResponse = mock(JsonNode.class, "firstPageResponseMock");
        JsonNode secondPageResponse = mock(JsonNode.class, "secondPageResponseMock");
        JsonNode returnMock = mock(JsonNode.class, "returnMock");

        when(firstPageResponse.get("fork")).thenReturn(returnMock);
        when(returnMock.asBoolean()).thenReturn(true);

        when(firstPageResponse.isEmpty()).thenReturn(false);
        when(secondPageResponse.isEmpty()).thenReturn(true);

        doReturn(firstPageResponse).when(this.userReposService).getReposByPage(userDto.username(),1);
        doReturn(secondPageResponse).when(this.userReposService).getReposByPage(userDto.username(),2);

        assertThat(this.userReposService.getUserRepos(userDto)).usingRecursiveComparison().isEqualTo(expectedUser);

    }

    @Test
    void testGetUserReposByPage() throws URISyntaxException, JsonProcessingException {
        ResponseEntity<?> returnMock = ResponseEntity.ok().body("{\"test\":\"test\"}");
        String username = "TestUserName";
        int page = 1;

        URI expectedUri = URI.create("https://api.github.com/users/"+username+"/repos?page="+page);
        JsonNode ExpectedReturn = new ObjectMapper().readTree("{\"test\":\"test\"}");

        try (MockedStatic<GitHubClient> mockUtils = mockStatic(GitHubClient.class)) {
            mockUtils.when(() -> GitHubClient.apiGetCallToGithub(expectedUri, userReposService.restTemplate)).thenReturn(returnMock);

            assertThat(this.userReposService.getReposByPage(username,page)).usingRecursiveComparison().isEqualTo(ExpectedReturn);
        }

    }

    @Test
    void testRunMethodHandleRepo() throws JsonProcessingException, URISyntaxException, ExecutionException, InterruptedException {
        JsonNode repo = new ObjectMapper().readTree("{\"name\":\"test\",\"fork\":false}");

        User user = new User("TestUserName");
        RepoModel repoModel = spy(new RepoModel("testRepoName"));
        UserReposServiceImpl.HandleRepo handleRepo = spy(new UserReposServiceImpl.HandleRepo(repo,user));

        doReturn(repoModel).when(handleRepo).createRepoModel(any());
        doNothing().when(repoModel).retrieveBranchesForRepo(anyString());

        handleRepo.run();

        assert user.getRepos().size() == 1;
        assertThat(user.getRepos().get(0)).isEqualTo(repoModel);

    }

    @Test
    void testCreateRepoModel() throws JsonProcessingException {
        JsonNode repo = new ObjectMapper().readTree("{\"name\":\"test\",\"fork\":false}");
        User user = new User("TestUserName");

        UserReposServiceImpl.HandleRepo handleRepo = new UserReposServiceImpl.HandleRepo(repo, user);

        RepoModel repoModel = handleRepo.createRepoModel("test");

        assertThat(repoModel.name).isEqualTo("test");
    }
}
