package com.jadarstudios.developercapes.cape;

import com.jadarstudios.developercapes.DevCapes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author jadar
 */
public enum CapeManager {
    INSTANCE;

    private HashMap<String, ICape> capes;

    private CapeManager() {
        this.capes = new HashMap<String, ICape>();
    }

    public void addCape(ICape cape) {
        if (!capes.containsValue(cape)) {
            capes.put(cape.getName(), cape);
        }
    }

    public void addCapes(Collection<ICape> capes) {
        for (ICape c : capes) {
            this.addCape(c);
        }
    }

    public ICape getCape(String capeName) {
        return capes.get(capeName);
    }

    public ICape newInstance(String name) {
        StaticCape cape = new StaticCape(name);
        this.capes.put(name, cape);
        return cape;
    }

    public ICape parse(String name, Object object) {
        ICape cape = null;
        if (!(object instanceof String)) {
            DevCapes.logger.error(String.format("Cape, %s, could not be parsed because it is not a String!", object));
            return cape;
        }

        try {
            cape = new StaticCape(name, new URL((String) object));
        } catch (MalformedURLException e) {
            DevCapes.logger.error(String.format("Are you crazy?? %s is not a valid URL!", (String) object));
            e.printStackTrace();
        } finally {
            return cape;
        }
    }
}
