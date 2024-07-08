package com.dataPractice.CrudOperations.Repository;

import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dataPractice.CrudOperations.Entities.People;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPullUserFromRepo(){
        Long userId = 1L;

        Optional<People> oUser = userRepository.getUserById(userId);

        assertThat(oUser.isPresent()).isTrue();
        People user = oUser.get();
        assertThat(user.firstName()).isEqualTo("Benjamin");
        assertThat(user.lastName()).isEqualTo("Triggiani");
        assertThat(user.age()).isEqualTo(27);
        assertThat(user.hobbies()).isEqualTo("biking, running, gaming, watching tv, studying");
    }

    @Test
    @Transactional
    void shouldCreateUser(){
        People user = new People(null, "Thomas", "Triggiani", 25, "anime, gaming, legos");
        Long newId = userRepository.createUser(user);

        assertThat(newId).isNotNull();

        Optional<People> oUser = userRepository.getUserById(newId);

        assertThat(oUser.isPresent()).isTrue();
    }

    @Test
    @Transactional
    void shouldDeleteUser(){
        int rowModified = userRepository.removeUserWithId(1L);

        assertThat(rowModified).isEqualTo(1);

        Optional<People> user = userRepository.getUserById(1L);

        assertThat(user.isPresent()).isFalse();

    }

    @Test
    @Transactional
    void shouldUpdateUser(){
        People user = new People(1L, "Benjamin", "Triggiani", 28, "biking, running, gaming, watching tv, studying");
        userRepository.updateUser(user);
        
        Optional<People> updatedUser = userRepository.getUserById(1L);
        assertThat(updatedUser.isPresent()).isTrue();

        assertThat(updatedUser.get().age()).isEqualTo(28);
    }
    
}
