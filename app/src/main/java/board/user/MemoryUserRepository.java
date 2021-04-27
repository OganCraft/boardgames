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
        addUser("jirka",  "Jirka");
        addUser("vojta",  "Vojta");
        addUser("betka",  "Bětka");
        addUser("kaja",  "Kája");
        addUser("sarka",  "Šárka");

        addUser("svata", "Svaťa");
        addUser("dita",  "Dita");
        addUser("aja",  "Ája");
        addUser("kami",  "Kamča");
        addUser("karol",  "Karolínka");
        addUser("paja", "Pája");
        addUser("jolca", "Jolča");
    }

    private void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    @Override
    public User findUserByLoginName(String loginName) {
        return users.get(loginName);
    }
}
