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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by spatium on 10.06.17.
 */
public class LandManager {

    private static LoadingCache<Data, OwnedLand> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<Data, OwnedLand>() {
                @Override
                public OwnedLand load(Data data) throws Exception {
                    OwnedLand ownedLand = Landlord.getInstance().getDatabase().getLand(data);
                    if (ownedLand != null)
                        return ownedLand;
                    else
                        throw new Exception("Land ist not owned!");
                }
            });

    /**
     * Factory method that creates a new OwnedLand instance given an owner name and chunk
     *
     * @param owner The owner of the land
     * @param c     The chunk this land represents
     * @return OwnedLand
     */
    public static OwnedLand createNewLand(UUID owner, Chunk c) {
        Data data = new Data(c.getWorld().getName(), c.getX(), c.getZ());
        OwnedLand lnd = new OwnedLand(data);
        lnd.setOwner(owner);
        lnd.setLandId(Landlord.getInstance().getDatabase().getFirstFreeLandID());
        lnd.setFlags(getDefaultFlags(lnd.getLandId()));
        lnd.setFriends(new ArrayList<>());
        cache.put(data, lnd);
        return lnd;
    }

    public static OwnedLand getApplicableLand(Location l) {
        return getLandFromDatabase(l.getWorld().getName(), l.getChunk().getX(), l.getChunk().getZ());
    }

    /**
     * Gets land from the database
     *
     * @param x         coord of chunk
     * @param z         coord of chunk
     * @param worldName of chunk
     * @return OwnedLand instance
     */
    public static OwnedLand getLandFromDatabase(String worldName, int x, int z) {
        Data data = new Data(worldName, x, z);
        try {
            return cache.get(data);
        } catch (ExecutionException e) {
            return null;
        }
    }


    private static Set<String> flags = Landlord.getInstance().getFlagManager().getRegisteredFlags().keySet();

    public static List<LandFlag> getDefaultFlags(int landid) {
        List<LandFlag> list = new ArrayList<>();

        int id = Landlord.getInstance().getDatabase().getFirstFreeFlagID();
        System.out.println(flags.size());
        for (String identifier : flags) {
            LandFlag flag = new LandFlag(landid, identifier, false, true, id);
            list.add(flag);
            id++;
        }
        return list;
    }
}
