package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IOwnedLand {
    String getName();

    String getOwnersString();

    String getMembersString();

    boolean isOwner(UUID uuid);

    UUID getOwner();

    void replaceOwner(UUID uuid);

    boolean isFriend(UUID uuid);

    Set<UUID> getFriends();

    void addFriend(UUID uuid);

    void removeFriend(UUID uuid);

    World getWorld();

    Chunk getChunk();

    void highlightLand(Chunk chunk, Player p, Particle pa);

    void highlightLand(Chunk chunk, Player p, Particle e, int amt);

    void highlightLand(Player p, Particle e);

    Location getALocation();

    boolean contains(int x, int y, int z);

    List<ILLFlag> getFlags();

    String getGreetMessage();

    void setGreetMessage(String newmsg);

    String getFarewellMessage();

    void setFarewellMessage(String newmsg);

    void toggleMob(IMob mob);

    boolean isMobDenied(IMob mob);


    double getPrice();

    void setPrice(double price);
}
