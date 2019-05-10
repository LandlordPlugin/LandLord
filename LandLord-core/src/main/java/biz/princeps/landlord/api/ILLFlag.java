package biz.princeps.landlord.api;

import org.bukkit.Material;

public interface ILLFlag {

    String getName();

    boolean toggleFriends();

    boolean toggleAll();

    boolean getFriendStatus();

    boolean getAllStatus();

    Material getMaterial();
}
