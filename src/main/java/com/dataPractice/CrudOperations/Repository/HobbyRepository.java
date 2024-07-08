package com.dataPractice.CrudOperations.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
/**
 * Repository component for persistenly managing hobbies.
 * 
 *  Sample user_with_hobbies view:
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
 * @author Benjamin Triggiani
 */
@Repository
public class HobbyRepository {

    private JdbcTemplate jdbcTemplate;

    /**
     * Constructor for UserRepository
     * 
     * @param jdbcTemplate tool for making sql commands
     */
    public HobbyRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * adds a new hobby to the hobbies table.
     * 
     * @param hobbyName the name of the hobby
     * @return the ID of the created hobby
     */
    public Long addHobby(String hobbyName){
        String insertHobbySql = """
            INSERT INTO hobbies (hobby_name)
            VALUES (?);
            """;
        KeyHolder hobbKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertHobbySql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hobbyName);
            return ps;
        }, hobbKeyHolder);
        Long newHobbyId = hobbKeyHolder.getKey().longValue();
        return newHobbyId;
    }

    /**
     * Finds the hobby_id by the hobby_name.
     * 
     * @param hobbyName The name of the hobby
     * @return the ID of the hobby
     */
    public Long getHobbyId(String hobbyName){
        String getHobbyId = """
                SELECT hobby_id
                FROM hobbies
                WHERE hobby_name = ?;
                """;

        return jdbcTemplate.queryForObject(getHobbyId, Long.class, hobbyName);
    }
}
