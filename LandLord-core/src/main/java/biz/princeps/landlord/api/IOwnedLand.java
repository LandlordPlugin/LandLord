package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 06-05-19
 */
public interface IOwnedLand {
    String getName();

    boolean isOwner(UUID uuid);

    UUID getOwner();

    boolean isMember(UUID uuid);

    Set<UUID> getMembers();

    void addFriend(UUID uuid);

    void removeFriend(UUID uuid);

    World getWorld();

    Chunk getChunk();

    Set<IFlag> getFlags();

    // IFlag getFlag(IFlag flag);
}
