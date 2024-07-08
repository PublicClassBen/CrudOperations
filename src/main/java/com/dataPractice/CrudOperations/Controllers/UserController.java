package com.dataPractice.CrudOperations.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.dataPractice.CrudOperations.Entities.User;
import com.dataPractice.CrudOperations.Service.UserService;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * RestController for managing user related operations
 * @author Benjamin Triggiani
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    
    /**
     * Constructor for UserController
     * 
     * @param userService the user service to be used for handling user operations
     */
    public UserController(UserService userService){
        this.userService = userService;
    }

    /**
     * Gets a user by their ID.
     * 
     * @param userId the ID of the user requested
     * @return A responseEntity if the user is found with a status code of 200 ok. If not found, a responseEntity containing a status code of 404
     */
    @GetMapping()
    private ResponseEntity<User> getUserById(@RequestParam Long userId){
        User user = userService.getUserById(userId);
        
        if(user != null)
            return ResponseEntity.ok(user);
        else
            return ResponseEntity.notFound().build();

    }

    /**
     * Adds a user to a database.
     * @param user the user to be added to the database. 
     * @return a responseEnity with a 201 created code. Throws  500 internal server error code if fails.
     */
    @PostMapping()
    public ResponseEntity<Void> createUser(@RequestBody User user){
        Long id = userService.createUser(user);
        URI uri = UriComponentsBuilder
        .fromPath("/user")
        .queryParam("userId", "{id}")
        .buildAndExpand(id)
        .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);
        return ResponseEntity.noContent().headers(headers).build();
    }

    /**
     * Deletes a user from the database.
     * @param userId the ID of the user that should be deleted.
     * @return a status code of 201 created, or a 404 not found if the user could not be found in the database.
     */
    @DeleteMapping()
    private ResponseEntity<Void> deleteUserById(@RequestParam Long userId){
        try{
            userService.deleteUserById(userId);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates the user in the database.
     * @param user the user to be updated. The userId porition of this user should not be null.
     * @return a status code of 201 created if successful, 500 internal server error code if failed.
     */
    @PutMapping()
    private ResponseEntity<Void> updateUser(@RequestBody User user){

        userService.updateUser(user);
        return ResponseEntity.noContent().build();
    }
    

}
