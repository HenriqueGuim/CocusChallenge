package pt.cocus.cocuschallenge.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pt.cocus.cocuschallenge.model.UserDto;
import pt.cocus.cocuschallenge.service.UserReposServiceImpl;

@RestController
@RequestMapping("/repos")
@AllArgsConstructor
@Slf4j
public class ReposController {
    private final UserReposServiceImpl userReposService;

    @GetMapping
    public ResponseEntity<?> getRepos(@RequestBody UserDto userDto) {
        ResponseEntity<?> responseEntity;
        try {

            responseEntity = ResponseEntity.ok(userReposService.getUserRepos(userDto));


        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {

                log.error("User not found: " + e.getMessage());

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\": 404, \"Message\":\"User not found\"}");
            }

            log.error("Internal Error: " + e.getMessage());

            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\": 500, \"Message\":\"Internal Error\"}");

        } catch (Exception e) {

            log.error("Internal Error: " + e.getMessage());

            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\": 500, \"Message\":\"Internal Error\"}");
        }

        return responseEntity;
    }
}
