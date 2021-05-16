package board.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hard coded list of users.
 */
public class MemoryUserRepository implements UserRepository {
    private final Map<String, User> users;

    public MemoryUserRepository() {
        users = new LinkedHashMap<>();

        // create users
        addUser("jirka",  "Jirka");
        addUser("vojta",  "Vojta");
        addUser("betka",  "Bětka Ku");
        addUser("kaja",  "Kája");
        addUser("sarka",  "Šárka");

        addUser("svata", "Svaťa");
        addUser("dita",  "Dita");
        addUser("aja",  "Ája");
        addUser("kami",  "Kamča");
        addUser("karol",  "Karolínka");
        addUser("betka2", "Bětka Kr");
        addUser("jolca", "Jolča");
        addUser("paja", "Pája");
    }

    private void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    @Override
    public User findUserByLoginName(String loginName) {
        return users.get(loginName);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(this.users.values());
    }
}
