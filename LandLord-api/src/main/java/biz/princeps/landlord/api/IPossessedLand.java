package biz.princeps.landlord.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IPossessedLand extends ILand {

    String getOwnerName();

    String getMembersName();

    boolean isOwner(UUID uuid);

    UUID getOwner();

    boolean isFriend(UUID uuid);

    Set<UUID> getFriends();

    void addFriend(UUID uuid);

    void removeFriend(UUID uuid);

    List<ILLFlag> getFlags();

    String getGreetMessage();

    void setGreetMessage(String newmsg);

    String getFarewellMessage();

    void setFarewellMessage(String newmsg);

    void toggleMob(IMob mob);

    void addLand(IPossessedLand land);

    void removeLand(String name);

    Set<String> getAllLandNames();
}
