package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.IOwnedLand;
import com.google.common.collect.Sets;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LandCache {

    private final Map<String, IOwnedLand> indexLandname = new HashMap<>();
    private final Map<UUID, Set<IOwnedLand>> indexPlayer = new HashMap<>();
    private final Map<World, Set<IOwnedLand>> indexWorld = new HashMap<>();

    public void add(IOwnedLand land) {
        if (land == null) {
            throw new RuntimeException("Cant Insert a null land!");
        }

        this.indexLandname.put(land.getName(), land);
        indexPlayer.computeIfAbsent(land.getOwner(), uuid -> Sets.newHashSet())
                .add(land);
        indexWorld.computeIfAbsent(land.getWorld(), uuid -> Sets.newHashSet())
                .add(land);
    }

    public IOwnedLand getLand(String name) {
        return indexLandname.get(name);
    }

    public Set<IOwnedLand> getLands(UUID uuid) {
        Set<IOwnedLand> playerLands = indexPlayer.get(uuid);
        return playerLands == null ? Collections.emptySet() : playerLands;
    }

    public Set<IOwnedLand> getLands(World world) {
        Set<IOwnedLand> worldLands = indexWorld.get(world);
        return worldLands == null ? Collections.emptySet() : worldLands;
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
