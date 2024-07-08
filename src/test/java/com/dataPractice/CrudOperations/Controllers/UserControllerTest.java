package com.dataPractice.CrudOperations.Controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dataPractice.CrudOperations.Entities.User;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnAUserWithHobbies(){
        ResponseEntity<String> response = restTemplate.getForEntity("/user?userId=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext dc = JsonPath.parse(response.getBody());

        Number userId = dc.read("$.userId");
        assertThat(userId).isEqualTo(1);

        String firstName = dc.read("$.firstName");
        assertThat(firstName).isEqualTo("Benjamin");
        
        String lastName = dc.read("$.lastName");
        assertThat(lastName).isEqualTo("Triggiani");

        Number age = dc.read("$.age");
        assertThat(age).isEqualTo(27);

        String hobbies = dc.read("$.hobbies");
        assertThat(hobbies).isEqualTo("biking, running, gaming, watching tv, studying");
        
    }

    @Test
    void shouldReturnNotFoundWhenRequestingAUserThatDoesntExists(){
        ResponseEntity<String> response = restTemplate.getForEntity("/user?userId=999999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void shouldAddUserAndAllHobbies(){
        User user = new User(null, "Thomas", "Triggiani", 25, "anime, gaming, legos");
        ResponseEntity<Void> response = restTemplate.postForEntity("/user", user, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = restTemplate.getForEntity(response.getHeaders().getLocation(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        restTemplate.delete(response.getHeaders().getLocation());
    }

    @Test
    void shouldDeleteAUser(){
        User user = new User(null, "Thomas", "Triggiani", 25, "anime, gaming, legos");
        ResponseEntity<Void> response = restTemplate.postForEntity("/user", user, Void.class);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(response.getHeaders().getLocation(), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void returnNotFoundWhenDeletingUserThatDoesNotExist(){
        ResponseEntity<Void> response = restTemplate.exchange("/user?userId=999999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateExistingUser(){
        User user = new User(1L, "Benjamin", "Triggiani", 28, "biking, running, gaming, watching tv, studying");
        HttpEntity<User> httpEntity = new HttpEntity<>(user);
        ResponseEntity<Void> response = restTemplate.exchange("/user", HttpMethod.PUT, httpEntity,Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/user?userId=1", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext dc = JsonPath.parse(getResponse.getBody());
        Number age = dc.read("$.age");
        assertThat(age).isEqualTo(28);

        user = new User(1L, "Benjamin", "Triggiani", 27, "biking, running, gaming, watching tv, studying");
        httpEntity = new HttpEntity<>(user);
        response = restTemplate.exchange("/user", HttpMethod.PUT, httpEntity,Void.class);

    }

}
