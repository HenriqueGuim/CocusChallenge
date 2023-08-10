package pt.cocus.cocuschallenge.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.cocus.cocuschallenge.model.UsernameModel;
import pt.cocus.cocuschallenge.service.UserReposService;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/repos")
@AllArgsConstructor
public class ReposController {
    private final UserReposService userReposService;

    @GetMapping
    public ResponseEntity<?> getRepos(@RequestBody UsernameModel usernameModel) {
        try {
            return ResponseEntity.ok(userReposService.getUserRepos(usernameModel));
        } catch (URISyntaxException | JsonProcessingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
