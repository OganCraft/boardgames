package board.user;

/**
 * Database of users.
 */
public interface UserRepository {
    /**
     * Find a user by its user name.
     * @param loginName user name
     * @return instance of {@link User} if the user exists
     */
    User findUserByLoginName(String loginName);
}
