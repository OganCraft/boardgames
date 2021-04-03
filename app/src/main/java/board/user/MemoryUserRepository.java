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
        users.put("betka", new User("Bětka"));
        users.put("kaja", new User("Kája"));
        users.put("sarka", new User("Šárka"));
        
        users.put("svata", new User("Svaťa"));
        users.put("dita", new User("Dita"));
        users.put("aja", new User("Ája"));
        users.put("kami", new User("Kamča"));
        users.put("karol", new User("Karolínka"));
    }

    @Override
    public User findUserByLoginName(String loginName) {
        return users.get(loginName);
    }
}
