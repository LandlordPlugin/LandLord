package com.jcdesimp.landlord.persistantData;


import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;

/**
 * File created by jcdesimp on 4/14/14.
 */

public class LandFlag {

    private int id, landid;
    private String identifier;
    private boolean canEveryone;
    private boolean canFriends;

    public LandFlag(int landid, String identifier, boolean canEveryone, boolean canFriends, int id) {
        this.id = id;
        this.identifier = identifier;
        this.canEveryone = canEveryone;
        this.canFriends = canFriends;
        this.landid = landid;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLandid() {
        return landid;
    }

    public void setLandid(int landid) {
        this.landid = landid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean canEveryone() {
        return canEveryone;
    }

    public boolean canFriends() {
        return canFriends;
    }

    public Landflag getFlag() {
        return Landlord.getInstance().getFlagManager().getRegisteredFlags().get(identifier);
    }

    public void setCanFriends(boolean canFriends) {
        this.canFriends = canFriends;
    }

    public void setCanEveryone(boolean canEveryone) {
        this.canEveryone = canEveryone;
    }
}
