package pt.cocus.cocuschallenge.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class GitHubClient {

    public static ResponseEntity<String> apiGetCallToGithub(URI uri, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + System.getenv("GITHUB_TOKEN"));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }
}
