package biz.princeps.landlord.protection;

import com.google.common.collect.Sets;
import org.bukkit.World;

import java.util.*;

public class LandCache {

    private Map<String, IOwnedLand> indexLandname = new HashMap<>();
    private Map<UUID, Set<IOwnedLand>> indexPlayer = new HashMap<>();
    private Map<World, Set<IOwnedLand>> indexWorld = new HashMap<>();

    public void add(IOwnedLand land) {
        if (land == null) {
            throw new RuntimeException("Cant Insert a null land!");
        }

        this.indexLandname.put(land.getName(), land);

        if (indexPlayer.containsKey(land.getOwner())) {
            indexPlayer.get(land.getOwner()).add(land);
        } else {
            indexPlayer.put(land.getOwner(), Sets.newHashSet(land));
        }

        if (indexWorld.containsKey(land.getWorld())) {
            indexWorld.get(land.getWorld()).add(land);
        } else {
            indexWorld.put(land.getWorld(), Sets.newHashSet(land));
        }
    }

    public IOwnedLand getLand(String name) {
        return indexLandname.get(name);
    }

    public Set<IOwnedLand> getLands(UUID uuid) {
        if (indexPlayer.get(uuid) == null) return new HashSet<>();
        return indexPlayer.get(uuid);
    }

    public Set<IOwnedLand> getLands(World w) {
        if (indexWorld.get(w) == null) return new HashSet<>();
        return indexWorld.get(w);
    }

    public boolean contains(String name) {
        return indexLandname.containsKey(name);
    }

    public void remove(String name) {
        IOwnedLand toRemove = indexLandname.get(name);

        indexPlayer.get(toRemove.getOwner()).remove(toRemove);
        indexWorld.get(toRemove.getWorld()).remove(toRemove);
        indexLandname.remove(name);
    }
}
