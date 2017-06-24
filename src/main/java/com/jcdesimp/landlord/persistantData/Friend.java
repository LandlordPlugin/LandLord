package com.jcdesimp.landlord.persistantData;


import org.bukkit.Bukkit;

import java.util.UUID;


/**
 * Friend object
 */

public class Friend {


    private int id;

    private UUID uuid;

    public Friend(UUID uniqueId) {
        this.uuid = uniqueId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
}
