package board.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Hard coded list of users.
 */
public class MemoryUserRepository implements UserRepository {
    private Map<String, User> users;

    public MemoryUserRepository() {
        users = new HashMap<>();

        // create users
        users.put("jirka", new User("Jirka"));
        users.put("vojta", new User("Vojta"));
    }

    @Override
    public User findUserByLoginName(String loginName) {
        return users.get(loginName);
    }
}
