package pt.cocus.cocuschallenge.modeltests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import pt.cocus.cocuschallenge.model.Branch;
import pt.cocus.cocuschallenge.model.RepoModel;
import pt.cocus.cocuschallenge.client.GitHubClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

@SpringBootTest
public class RepoModelTests {
    RepoModel repoModel = spy(RepoModel.class);

    @Test
    void testRetrieveBranchesForRepo() throws URISyntaxException, ExecutionException, JsonProcessingException, InterruptedException {
        this.repoModel.name = "testRepoName";
        ResponseEntity<?> firstReturnMock = ResponseEntity.ok().body("[\n{\n\"name\":\"branchName\",\n\"commit\":{\n\"sha\":\"testSha\"\n}\n}\n]");
        ResponseEntity<?> secondReturnMock = ResponseEntity.ok().body("[]");
        URI expectedUri1 = URI.create("https://api.github.com/repos/testUserName/testRepoName/branches?page=1");
        URI expectedUri2 = URI.create("https://api.github.com/repos/testUserName/testRepoName/branches?page=2");
        Branch expectedBranch = new Branch("branchName", "testSha");



        try (MockedStatic<GitHubClient> mockUtils = mockStatic(GitHubClient.class)) {
            mockUtils.when(() -> GitHubClient.apiGetCallToGithub(expectedUri1, repoModel.restTemplate)).thenReturn(firstReturnMock);
            mockUtils.when(() -> GitHubClient.apiGetCallToGithub(expectedUri2, repoModel.restTemplate)).thenReturn(secondReturnMock);

            repoModel.retrieveBranchesForRepo("testUserName");
        }

        assert repoModel.branches.size() == 1;
        assertThat(repoModel.branches.get(0)).usingRecursiveComparison().isEqualTo(expectedBranch);
    }

}
