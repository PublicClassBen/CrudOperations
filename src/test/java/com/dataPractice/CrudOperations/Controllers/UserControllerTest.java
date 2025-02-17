package com.dataPractice.CrudOperations.Controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.dataPractice.CrudOperations.Entities.People;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnAUserWithHobbies(){
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .getForEntity("/user?userId=1", String.class);

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
        
        String owner = dc.read("$.owner");
        assertThat(owner).isEqualTo("btriggiani");
    }

    @Test
    void shouldReturnNotFoundWhenRequestingAUserThatDoesntExists(){
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .getForEntity("/user?userId=999999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void shouldAddUserAndAllHobbies(){
        People user = new People(null, "Thomas", "Triggiani", 25, "anime, gaming, legos", null);
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .postForEntity("/user", user, Void.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .getForEntity(response.getHeaders().getLocation(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Void> deleteResponse = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .exchange(response.getHeaders().getLocation(), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteAUser(){
        People user = new People(null, "Thomas", "Triggiani", 25, "anime, gaming, legos", "btriggiani");
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .postForEntity("/user", user, Void.class);

        ResponseEntity<Void> deleteResponse = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .exchange(response.getHeaders().getLocation(), HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void returnNotFoundWhenDeletingUserThatDoesNotExist(){
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .exchange("/user?userId=999999", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateExistingUser(){
        People user = new People(1L, "Benjamin", "Triggiani", 28, "biking, running, gaming, watching tv, studying", null);
        HttpEntity<People> httpEntity = new HttpEntity<>(user);
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .exchange("/user", HttpMethod.PUT, httpEntity,Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")
        .getForEntity("/user?userId=1", String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext dc = JsonPath.parse(getResponse.getBody());
        Number age = dc.read("$.age");
        assertThat(age).isEqualTo(28);

        user = new People(1L, "Benjamin", "Triggiani", 27, "biking, running, gaming, watching tv, studying", null);
        httpEntity = new HttpEntity<>(user);
        response = restTemplate
        .withBasicAuth("btriggiani", "notAPassword!")  
        .exchange("/user", HttpMethod.PUT, httpEntity,Void.class);

    }

    @Test
    void shouldReturnUnauthorizedIfPasswordIsIncorrect(){
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("btriggiani", "BAD_PASSWORD")
        .getForEntity("/user?userId=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturnUnauthorizedIfUsernameIsIncorrect(){
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("BAD_USERNAME", "notAPassword!")
        .getForEntity("/user?userId=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldNotBeAbleToAccessUsersTheyDoNotOwn(){
        ResponseEntity<String> response = restTemplate
        .withBasicAuth("atriggiani", "notAPassword!")
        .getForEntity("/user?userId=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotBeAbleToUpdateUsersTheyDoNotOwn(){
        People user = new People(1L, "Benjamin", "Triggiani", 27, "biking, running, gaming, watching tv, studying", null);
        HttpEntity<People> httpEntity = new HttpEntity<>(user);
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("atriggiani", "notAPassword!")  
        .exchange("/user", HttpMethod.PUT, httpEntity,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotBeAbleToDeleteUsersTheyDoNotOwn(){
        ResponseEntity<Void> response = restTemplate
        .withBasicAuth("atriggiani", "notAPassword!")
        .exchange("/user?userId=1", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
