package pt.cocus.cocuschallenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import pt.cocus.cocuschallenge.controllers.ReposController;
import pt.cocus.cocuschallenge.model.User;
import pt.cocus.cocuschallenge.model.UserDto;
import pt.cocus.cocuschallenge.service.UserReposServiceImpl;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ControllersTests {
    UserReposServiceImpl userReposService;
    ReposController reposController;

    @BeforeEach
    public void setUp() {
        this.userReposService = mock(UserReposServiceImpl.class, "userReposServiceMock");
        this.reposController = new ReposController(userReposService);
    }


    @Test
    void testGetReposValidRequest() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {
        UserDto userDto = new UserDto("TestUserName");
        User expectedUser = new User(userDto.username());
        ResponseEntity<?> expectedResponse = ResponseEntity.ok(expectedUser);

        when(this.userReposService.getUserRepos(userDto)).thenReturn(expectedUser);

        assertThat(this.reposController.getRepos(userDto)).isEqualTo(expectedResponse);

    }

    @Test
    void testGetReposInvalidNameRequest() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {
        UserDto userDto = new UserDto("TestUserName");
        ResponseEntity<?> expectedResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\": 404, \"Message\":\"User not found\"}");

        when(this.userReposService.getUserRepos(userDto)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThat(this.reposController.getRepos(userDto)).isEqualTo(expectedResponse);

    }

    @Test
    void testGetReposInternalApiCallError() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {
        UserDto userDto = new UserDto("TestUserName");
        ResponseEntity<?> expectedResponse = ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\": 500, \"Message\":\"Internal Error\"}");

        when(this.userReposService.getUserRepos(userDto)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE));

        assertThat(this.reposController.getRepos(userDto)).isEqualTo(expectedResponse);

    }

    @Test
    void testGetReposInternalError() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {
        UserDto userDto = new UserDto("TestUserName");
        ResponseEntity<?> expectedResponse = ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"status\": 500, \"Message\":\"Internal Error\"}");

        when(this.userReposService.getUserRepos(userDto)).thenThrow(new URISyntaxException("testInput","testReason"));

        assertThat(this.reposController.getRepos(userDto)).isEqualTo(expectedResponse);
    }


}
