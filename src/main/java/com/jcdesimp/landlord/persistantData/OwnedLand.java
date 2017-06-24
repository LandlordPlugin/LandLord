package com.jcdesimp.landlord.persistantData;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.landManagement.Landflag;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnedLand {

    private List<Friend> friends;
    private List<LandFlag> flags;
    private int landid;

    private UUID owner;

    private Data data;


    public OwnedLand(Data data) {
        this.data = data;
    }


    public int getLandId() {
        return landid;
    }

    public void setLandId(int id) {
        this.landid = id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID ownerUUID) {
        this.owner = ownerUUID;
    }

    public String getOwnerUsername() {
        String unknownUser = "Unknown";
        OfflinePlayer op = Bukkit.getOfflinePlayer(owner);
        if (!op.hasPlayedBefore() && !op.isOnline()) {
            return ChatColor.ITALIC + unknownUser;
        }
        return op.getName();
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public void setFlags(List<LandFlag> flags) {
        this.flags = flags;
    }

    public Data getData() {
        return data;
    }

    public Chunk getChunk() {
        World world = Bukkit.getServer().getWorld(data.getWorld());
        return world.getChunkAt(data.getX(), data.getZ());
    }


    public boolean hasPermTo(Player player, Landflag lf) {
        if (player.hasPermission("landlord.admin.bypass") || player.getUniqueId().equals(owner)) {
            return true;
        }
        if (isFriend(player.getUniqueId())) {
            return getFlag(lf).canFriends();
        }
        return getFlag(lf).canEveryone();
    }

    public LandFlag getFlag(Landflag flagin) {
        for (LandFlag flag : flags) {
            if (flag.getIdentifier().equals(flagin.getClass().getSimpleName()))
                return flag;
        }
        return null;
    }

    public boolean canEveryone(Landflag lf) {
        return getFlag(lf).canEveryone();
    }


    /**
     * Attempt to add a friend
     * Checks to make sure player is not already a friend of and instance
     *
     * @param f Friend to be added
     * @return boolean true if success false if already a friend
     */
    public boolean addFriend(Friend f) {
        if (!isFriend(f.getUuid())) {
            friends.add(f);
            return true;
        }
        return false;
    }

    /**
     * Removes a friend
     *
     * @param id friend to remove
     * @return boolean
     */
    public boolean removeFriend(UUID id) {
        if (isFriend(id)) {
            Landlord.getInstance().getDatabase().removeFriend(landid, id);
            this.friends.remove(id);
            return true;
        }
        return false;
    }

    /**
     * Returns whether or not a player is
     * a friend of this land
     *
     * @return boolean is a friend or not
     */
    public boolean isFriend(UUID id) {
        for (Friend f : friends) {
            if (f.getUuid().equals(id))
                return true;
        }
        return false;
    }

    public void delete() {
        Landlord.getInstance().getDatabase().removeLand(landid);
        Landlord.getInstance().getLandManager().removeFromCache(data);
        if (Bukkit.getPlayer(owner) != null)
            Landlord.getInstance().getLandManager().updateLandCount(owner, Landlord.getInstance().getLandManager().getLandCount(owner) - 1);
    }


    /**
     * Highlights the border around the chunk with a particle effect.
     *
     * @param p player
     * @param e effect to play
     */
    public static void highlightLand(Player p, Particle e) {
        highlightLand(p, e, 5);
    }

    public static void highlightLand(Player p, Particle e, int amt) {
        if (!Landlord.getInstance().getConfig().getBoolean("options.particleEffects", true)) {
            return;
        }
        Chunk chunk = p.getLocation().getChunk();
        ArrayList<Location> edgeBlocks = new ArrayList<Location>();
        for (int i = 0; i < 16; i++) {
            for (int ii = -1; ii <= 10; ii++) {

                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 15).getLocation());
                edgeBlocks.add(chunk.getBlock(i, (int) (p.getLocation().getY()) + ii, 0).getLocation());
                edgeBlocks.add(chunk.getBlock(0, (int) (p.getLocation().getY()) + ii, i).getLocation());
                edgeBlocks.add(chunk.getBlock(15, (int) (p.getLocation().getY()) + ii, i).getLocation());
            }


        }
        //BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        for (Location edgeBlock : edgeBlocks) {
            edgeBlock.setZ(edgeBlock.getBlockZ() + .5);
            edgeBlock.setX(edgeBlock.getBlockX() + .5);

            //  p.spigot().playEffect(edgeBlock, e, 0, 0, 0.2f, 0.2f, 0.2f, 0.2f, amt, 20);
            p.getWorld().spawnParticle(e, edgeBlock, amt, 0.2, 0.2, 0.2, 20.0);
            //e.display(edgeBlock, 0.2f, 0.2f, 0.2f, 9.2f, amt, p);
            //p.playEffect(edgeBlocks.get(i), e, null);
        }

    }

    public List<LandFlag> getFlags() {
        return flags;
    }

    public void save() {
        Landlord.getInstance().getDatabase().save(this);
    }
}
