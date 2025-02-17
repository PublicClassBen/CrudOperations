package com.dataPractice.CrudOperations.Repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dataPractice.CrudOperations.Entities.People;
/**
 *  Respositiory componment for persistantly managing users within the user_with_hobbies table.
 * 
 * Sample user_with_hobbies view:
 * +---------+-----------+-----------+------+------------+------------------------------------------------+
 * | user_id | FirstName | LastName  | Age  | Owner      | hobbies                                        |
 * +---------+-----------+-----------+------+------------+------------------------------------------------+
 * |       1 | Benjamin  | Triggiani |   27 | btriggiani | biking, running, gaming, watching tv, studying |
 * +---------+-----------+-----------+------+------------+------------------------------------------------+
 * 
 * Sample people table:
 * +---------+------------+-----------+------+------------+
 * | user_id | first_name | last_name | age  | owner      |
 * +---------+------------+-----------+------+------------+
 * |       1 | Benjamin   | Triggiani |   27 | btriggiani |
 * +---------+------------+-----------+------+------------+
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
public class UserRepository {

    private JdbcTemplate jdbcTemplate;
    private HobbyRepository hobbyRepository;
    private UserHobbyAssociationRepository userHobbyAssociationRepository;
    
    /**
     * Constructor for UserRepository.
     * 
     * @param jdbcTemplate used to make sql requests
     * @param hobbyRepository repository for managing the hobby table
     * @param userHobbyAssociationRepository repository for managing the userHobbyAssociation table
     */
    public UserRepository(JdbcTemplate jdbcTemplate, HobbyRepository hobbyRepository, UserHobbyAssociationRepository userHobbyAssociationRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.hobbyRepository = hobbyRepository;
        this.userHobbyAssociationRepository = userHobbyAssociationRepository;
    }

    /**
     * Gets the user from the user table of the database.
     * 
     * @param id the ID of the user
     * @return an optional user, is not empty when user has been found
     */
    public Optional<People> getUserById(Long id, String owner){
        //created view in database for easy execution
        String sql = """
                SELECT user_Id, firstName, lastName, age, hobbies, owner
                FROM user_with_hobbies
                WHERE user_Id = ? AND owner = ?;
                """;
        try{
        People user = jdbcTemplate.queryForObject(sql, (rs, row) -> {
            return new People(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6));
        }, id, owner);
        return Optional.of(user);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    /**
     * Adds a new user to the database.
     * 
     * @param user the user to be created. Expects database to create and return user ID. User id should be null.
     * @return the ID of the created user
     */
    public Long createUser(People user){
        try{
            String insertUserSql = """
                INSERT INTO people(first_name, last_name, age, owner)
                VALUES(?, ?, ?, ?);
                """;            
            KeyHolder userKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.firstName());
                ps.setString(2, user.lastName());
                ps.setInt(3, user.age());
                ps.setString(4, user.owner());
                return ps;
            }, userKeyHolder);

            long userId = userKeyHolder.getKey().longValue();

            String[] hobbies = user.hobbies().split(", ");
            
            for(int index = 0; index  < hobbies.length; index++){
                String hobby = hobbies[index];
                Long hobbyId;
                try {
                    hobbyId = hobbyRepository.getHobbyId(hobby);
                } catch (EmptyResultDataAccessException e) {
                    hobbyId = null;
                }
                if(hobbyId == null){
                    Long newHobbyId = hobbyRepository.addHobby(hobby);
                    userHobbyAssociationRepository.addUserHobbyAssociation(userId, newHobbyId);
                }else{
                    userHobbyAssociationRepository.addUserHobbyAssociation(userId, hobbyId);
                }
            }
            return userId;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * Adds new/existing user to the database. Has the ability to modify the user_id field.
     * 
     * @param user the user to be created. Should have user ID
     * @return the ID of the created user
     */
    public Long createUserIdOverride(People user){
        try{
            String insertUserSql = """
                INSERT INTO people(user_id, first_name, last_name, age, owner)
                VALUES(?, ?, ?, ?, ?);
                """;         
            KeyHolder userKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, user.userId());
                ps.setString(2, user.firstName());
                ps.setString(3, user.lastName());
                ps.setInt(4, user.age());
                ps.setString(5, user.owner());
                return ps;
            }, userKeyHolder);

            long userId = userKeyHolder.getKey().longValue();

            String[] hobbies = user.hobbies().split(", ");
            
            for(int index = 0; index  < hobbies.length; index++){
                String hobby = hobbies[index];
                Long hobbyId;
                try {
                    hobbyId = hobbyRepository.getHobbyId(hobby);
                } catch (EmptyResultDataAccessException e) {
                    hobbyId = null;
                }
                if(hobbyId == null){
                    Long newHobbyId = hobbyRepository.addHobby(hobby);
                    userHobbyAssociationRepository.addUserHobbyAssociation(userId, newHobbyId);
                }else{
                    userHobbyAssociationRepository.addUserHobbyAssociation(userId, hobbyId);
                }
            }
            return userId;
        }catch(Exception e){
            return null;
        }
    }
    
    /**
     * Deletes user by ID.
     * 
     * @param id the ID of the user who should be deleted
     * @return the number of rows modified
     */
    @Transactional
    public int removeUserWithId(Long id, String owner){
        String userSql = """
                DELETE FROM people 
                WHERE user_id = ? AND owner = ?;
                """;
        
        //made decision to not delete the predefined hobbies because other users might later have them
        int userRow = jdbcTemplate.update(userSql, id, owner);
        if(userRow > 0)
            userHobbyAssociationRepository.deleteAssociationWith(id);
        
        return userRow;
    }
    
    /**
     * Updates and existing user.
     * 
     * @param user the user to be updated
     */
    public void updateUser(People user){

        int rowmod = removeUserWithId(user.userId(), user.owner());
        if(rowmod == 0)
            throw new RuntimeException();
        createUserIdOverride(user);
    }
}
