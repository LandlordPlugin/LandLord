package com.jcdesimp.landlord.landManagement;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Data;
import com.jcdesimp.landlord.persistantData.LandFlag;
import com.jcdesimp.landlord.persistantData.OwnedLand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 10.06.17.
 */
public class LandManager implements Listener {

    private LoadingCache<Data, OwnedLand> cache = CacheBuilder.newBuilder()
            .maximumSize(Landlord.getInstance().getConfig().getInt("cacheSize"))
            .build(new CacheLoader<Data, OwnedLand>() {
                @Override
                public OwnedLand load(Data data) throws Exception {
                    OwnedLand ownedLand = Landlord.getInstance().getDatabase().getLand(data).get();
                    //   System.out.println("Get from cache called" + cache.size());
                    //   System.out.println(data.getWorld() + ":" + data.getX() + ":" + data.getZ());
                    //  System.out.println(data.hashCode() + " __  " + ownedLand.getData().hashCode());
                    if (ownedLand != null)
                        return ownedLand;
                    else
                        throw new Exception("Land ist not owned!");
                }
            });

    private HashMap<UUID, Integer> landcount = new HashMap<>();


    /**
     * Factory method that creates a new OwnedLand instance given an owner name and chunk
     *
     * @param owner The owner of the land
     * @param c     The chunk this land represents
     * @return OwnedLand
     */
    public OwnedLand createNewLand(UUID owner, Chunk c) {
        Data data = new Data(c.getWorld().getName(), c.getX(), c.getZ());
        OwnedLand lnd = new OwnedLand(data);
        lnd.setOwner(owner);
        lnd.setLandId(Landlord.getInstance().getDatabase().getFirstFreeLandID());
        lnd.setFlags(getDefaultFlags(lnd.getLandId()));
        lnd.setFriends(new ArrayList<>());
        cache.put(data, lnd);
        updateLandCount(owner, getLandCount(owner) + 1);
        return lnd;
    }

    public OwnedLand getApplicableLand(Location l) {
        return getLandFromCache(l.getWorld().getName(), l.getChunk().getX(), l.getChunk().getZ());
    }

    public OwnedLand getApplicableLand(Chunk chunk) {
        return getLandFromCache(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }


    /**
     * Gets land from the database
     *
     * @param x         coord of chunk
     * @param z         coord of chunk
     * @param worldName of chunk
     * @return OwnedLand instance
     */
    public OwnedLand getLandFromCache(String worldName, int x, int z) {
        Data data = new Data(worldName, x, z);
        OwnedLand toFind = null;
        try {
            toFind = cache.get(data);
        } catch (ExecutionException e) {
        } finally {
            return toFind;
        }
    }

    private static Set<String> flags = Landlord.getInstance().getFlagManager().getRegisteredFlags().keySet();

    public static List<LandFlag> getDefaultFlags(int landid) {
        List<LandFlag> list = new ArrayList<>();

        for (String identifier : flags) {
            LandFlag flag = new LandFlag(landid, identifier, false, true);
            list.add(flag);
        }
        return list;
    }


    public void removeFromCache(Data data) {
        cache.invalidate(data);
    }

    public void insertOrReplaceIntoCache(OwnedLand land) {
        OwnedLand lanny = cache.getIfPresent(land.getData());
        if (lanny == null)
            cache.put(land.getData(), land);
    }


    // player land count manager
    public void insertLandCount(UUID id) {
        landcount.put(id, Landlord.getInstance().getDatabase().getLands(id).size());
    }

    public void removeLandCount(UUID id) {
        landcount.remove(id);
    }


    public int getLandCount(UUID id) {
        return landcount.get(id);
    }

    public void updateLandCount(UUID id, int i) {
        landcount.replace(id, i);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        insertLandCount(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        removeLandCount(e.getPlayer().getUniqueId());
    }
}
