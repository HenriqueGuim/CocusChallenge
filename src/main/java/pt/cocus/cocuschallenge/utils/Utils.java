package pt.cocus.cocuschallenge.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class Utils {

    public static ResponseEntity<String> apiGetCallToGithub(URI uri, RestTemplate restTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + System.getenv("GITHUB_TOKEN"));

        HttpEntity<String> entity = new HttpEntity<String>("body", headers);

        return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    }
}
