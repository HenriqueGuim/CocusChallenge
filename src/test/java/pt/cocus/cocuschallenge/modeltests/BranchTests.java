package pt.cocus.cocuschallenge.modeltests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cocus.cocuschallenge.model.Branch;

@SpringBootTest
public class BranchTests {

    @Test
    void TestNewBranch() {
        Branch branch = new Branch("TestBranchName", "TestBranchSha");

        assert branch.name().equals("TestBranchName");
        assert branch.sha().equals("TestBranchSha");
    }
}
