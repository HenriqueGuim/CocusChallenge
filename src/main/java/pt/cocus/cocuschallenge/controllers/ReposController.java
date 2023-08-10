package pt.cocus.cocuschallenge.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pt.cocus.cocuschallenge.model.UsernameModel;
import pt.cocus.cocuschallenge.service.UserReposServiceImpl;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/repos")
@AllArgsConstructor
public class ReposController {
    private final UserReposServiceImpl userReposService;

    @GetMapping
    public ResponseEntity<?> getRepos(@RequestBody UsernameModel usernameModel, @RequestHeader("Accept") String accept) {

        if (!accept.equals("*/*") && !accept.equals ("application/json")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\": 404, \"Message\":\"Header Accept format not compatible\"}");
        }
        try {

            return ResponseEntity.ok(userReposService.getUserRepos(usernameModel));

        } catch (URISyntaxException | JsonProcessingException | InterruptedException | ExecutionException e) {

            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\": 500, \"Message\":\"Internal Error\"}");

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\": 404, \"Message\":\"User not found\"}");
            }
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\": 500, \"Message\":\"Internal Error\"}");

        }
    }
}
