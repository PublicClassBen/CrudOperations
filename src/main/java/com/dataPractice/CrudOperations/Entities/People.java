package com.dataPractice.CrudOperations.Entities;
/**
 * A entity that represents a user and their hobbies
 * 
 * @author Benjamin Triggiani
 */
public record People(Long userId, String firstName, String lastName, int age, String hobbies) {

}
