package org.skysongdev.skysongchattweaks.database;

import java.util.ArrayList;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class Profile {
    private String uuid;
    private String profile;
    private ArrayList<String> permissions;

    public Profile(String uuid, String profile, ArrayList<String> permissions) {
        this.uuid = uuid;
        this.profile = profile;
        this.permissions = permissions;
    }

    public String getUUID() {
        return uuid;
    }

    public String getProfile() {
        return profile;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String permission) {
        permissions.add(permission);
        try{
        getPlugin().getDatabase().updateProfile(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
