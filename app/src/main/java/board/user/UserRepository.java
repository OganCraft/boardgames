package board.user;

import java.util.List;

/**
 * Database of users.
 */
public interface UserRepository {
    /**
     * Find a user by its user name.
     *
     * @param loginName user name
     * @return instance of {@link User} if the user exists
     */
    User findUserByLoginName(String loginName);

    /**
     * Get all users.
     *
     * @return list of users allowed to log in
     */
    List<User> getAllUsers();
}
