package com.jadarstudios.developercapes.user;

import com.jadarstudios.developercapes.DevCapes;
import com.jadarstudios.developercapes.cape.CapeManager;
import com.jadarstudios.developercapes.cape.ICape;

import java.util.HashMap;
import java.util.Set;

/**
 * @author jadar
 */
public enum UserManager {
    INSTANCE;

    protected HashMap<String, User> users;

    private UserManager() {
        this.users = new HashMap<String, User>();
    }

    public User getUser(String username) {
        return this.users.get(username);
    }

    public void addUser(User user) throws NullPointerException {
        if (user == null || user.username == null || user.username.isEmpty()) {
            DevCapes.logger.error("Cannot add a null user.");
            throw new NullPointerException();
        }

        this.users.put(user.username, user);
        CapeManager.INSTANCE.addCapes(user.capes);
    }

    public void addUsers(Set<User> users) throws Exception {
        for (User u : users) {
            this.addUser(u);
        }
    }

    public User newInstance(String username) {
        User instance = null;
        if (this.users.containsKey(username)) {
            instance = this.getUser(username);
        } else {
            instance = new User(username);
            this.users.put(username, instance);
        }

        return instance;
    }

    public User parse(Object user, Object cape) {
        User userInstance = null;
        if (!(user instanceof String)) {
            DevCapes.logger.error(String.format("User %s could not be parsed because it was not a String!", user.toString()));
            return userInstance;
        }

        userInstance = new User((String) user);
        ICape capeInstance = CapeManager.INSTANCE.parse((String) user, cape);

        if (capeInstance != null) {
            userInstance.capes.add(capeInstance);
        } else {
            DevCapes.logger.error(String.format("Error parsing cape, %s", cape.toString()));
        }

        return userInstance;
    }
}
