package com.jadarstudios.developercapes.user;

import com.jadarstudios.developercapes.DevCapes;
import com.jadarstudios.developercapes.cape.CapeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author jadar
 */
public enum GroupManager {
    INSTANCE;

    private HashMap<String, Group> groups;

    private GroupManager() {
        this.groups = new HashMap<String, Group>();
    }

    public void addGroup(Group group) {
        groups.put(group.name, group);

        try {
            UserManager.INSTANCE.addUsers(new HashSet<User>(group.users.values()));
            CapeManager.INSTANCE.addCape(group.cape);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Group getGroup(String capeName) {
        return groups.get(capeName);
    }


    public Group newInstance(String name) {
        if (this.getGroup(name) != null) {
            return this.getGroup(name);
        }
        Group group = new Group(name);
        return group;
    }

    public Group parse(String name, Map<String, Object> data) {
        Group group = new Group(name);

        Object usersObj = data.get("users");
        Object capeUrlObj = data.get("capeUrl");

        if (!(usersObj instanceof ArrayList) || !(capeUrlObj instanceof String)) {
            DevCapes.logger.error(String.format("Group %s could not be parsed because it either is invalid or missing elements.", name));
            return null;
        }

        ArrayList users = (ArrayList)usersObj;
        String capeUrl = (String)capeUrlObj;

        group.cape = CapeManager.INSTANCE.parse(name, capeUrl);

        for (Object obj : users) {
            User user = UserManager.INSTANCE.parse(obj, group.cape);
            if (user != null) {
                group.addUser(user);
            } else {
                continue;
            }
        }

        return group;
    }
}
