package com.dataPractice.CrudOperations.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
/**
 * Repository for persistently managing the user_hobby_association table.
 * 
 * Since a user can have many hobbies, and a single hobby can have many users, a relational table was
 * created to manage this relationship.
 * 
 * Sample user_with_hobbies view:
 * +--------+-----------+-----------+------+------------------------------------------------+
 * | userId | firstName | lastName  | age  | hobbies                                        |
 * +--------+-----------+-----------+------+------------------------------------------------+
 * |      1 | Benjamin  | Triggiani |   27 | biking, running, gaming, watching tv, studying |
 * +--------+-----------+-----------+------+------------------------------------------------+
 * 
 * Sample users table:
 * +---------+------------+-----------+------+
 * | user_id | first_name | last_name | age  |
 * +---------+------------+-----------+------+
 * |       1 | Benjamin   | Triggiani |   27 |
 * +---------+------------+-----------+------+
 * 
 * Sample hobbies table
 * +----------+-------------+
 * | hobby_id | hobby_name  |
 * +----------+-------------+
 * |        1 | biking      |
 * |        2 | running     |
 * |        3 | gaming      |
 * |        4 | watching tv |
 * |        5 | studying    |
 * |        6 | anime       |
 * |        7 | legos       |
 * +----------+-------------+
 * 
 * Sample user_hobby_association table
 * +---------+----------+
 * | user_id | hobby_id |
 * +---------+----------+
 * |       1 |        1 |
 * |       1 |        2 |
 * |       1 |        3 |
 * |       1 |        4 |
 * |       1 |        5 |
 * +---------+----------+
 * 
 * @author Benjamin Triggiani
 */
@Repository
public class UserHobbyAssociationRepository {

    private JdbcTemplate jdbcTemplate;

    /**
     * Constructor for UserHobbyAssociationRepository
     * 
     * @param jdbcTemplate tool to run sql commands
     */
    public UserHobbyAssociationRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Adds a user hobby association 
     * 
     * @param userId the id of the user who has the hobby
     * @param hobbyId the id of the hobby
     * 
     * @return the number of rows modified
     */
    public int addUserHobbyAssociation(Long userId, Long hobbyId){
        String insertUserHobbyAssociationSql = """
                INSERT INTO user_hobby_association (user_id, hobby_id)
                VALUES (?, ?);
                """;
        return jdbcTemplate.update(insertUserHobbyAssociationSql, userId, hobbyId);
    }

    /**
     * Delete a users association to hobbies.
     * 
     * @param userId the id of the user
     * @return the number of rows modified
     */
    public int deleteAssociationWith(Long userId){
        String associationSql = """
                DELETE FROM user_hobby_association
                WHERE user_id = ?;
                """;
        return jdbcTemplate.update(associationSql, userId);
    }
}
