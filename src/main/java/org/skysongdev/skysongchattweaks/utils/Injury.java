package org.skysongdev.skysongchattweaks.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Injury {
    private String id;
    private String name;
    private String description;
    private String item;
    private List<String> permissions;
    private String severity;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getItem() {
        return item;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getSeverity() {
        return severity;
    }

    public Injury(String id, String name, String description, String item, List<String> permissions, String severity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.item = item;
        this.permissions = permissions;
        this.severity = severity;
    }

    public boolean doesPlayerHavePermission(Player player) {
        for(String permission : permissions) {
            if(player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }


}
