package com.dataPractice.CrudOperations.Service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataPractice.CrudOperations.Entities.People;
import com.dataPractice.CrudOperations.Repository.UserRepository;
/**
 * Service component for managing Users.
 * Any business logic for users should be done in this layer.
 * 
 * @author Benjamin Triggiani
 */
@Service
public class UserService {
    private UserRepository userRepository;
    /**
     * Constructor for UserService
     * 
     * @param userRepository the repository component for using for persistently managing Users
     */
    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Gets user by their ID.
     * 
     * @param id the ID of the user requested
     * @return the User from the database
     */
    public People getUserById(Long id){
        Optional<People> oUser = userRepository.getUserById(id);

        return oUser.isPresent()? oUser.get(): null;
    }

    /**
     * Inserts new user to the database.
     * 
     * @param user the user to be inserted
     * @return the ID of the created user
     */
    @Transactional
    public long createUser(People user){
        return userRepository.createUser(user);
    }

    /**
     * Deletes a user from the database.
     * 
     * @param userId the ID of the user to be deleted
     */
    @Transactional
    public void deleteUserById(Long userId){
        int rowsModified = userRepository.removeUserWithId(userId);
        if(rowsModified == 0)
            throw new RuntimeException("Attempting to Delete a user that does not exist with ID: " + userId + " Trace: ");
    }

    /**
     * Updates existing user in the database
     * 
     * @param user the new user content to be updated
     */
    @Transactional
    public void updateUser(People user){
        userRepository.updateUser(user);
    }

}
