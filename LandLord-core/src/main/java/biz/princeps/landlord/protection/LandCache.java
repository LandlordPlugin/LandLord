package biz.princeps.landlord.protection;

import biz.princeps.landlord.api.IPossessedLand;
import com.google.common.collect.Sets;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LandCache {

    private Map<String, IPossessedLand> indexLandname = new HashMap<>();
    private Map<UUID, Set<IPossessedLand>> indexPlayer = new HashMap<>();
    private Map<World, Set<IPossessedLand>> indexWorld = new HashMap<>();

    public void add(IPossessedLand land) {
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

    @Nullable
    public IPossessedLand getLand(String name) {
        return indexLandname.get(name);
    }

    @NotNull
    public Set<IPossessedLand> getLands(UUID uuid) {
        if (indexPlayer.get(uuid) == null) {
            return new HashSet<>();
        }
        return indexPlayer.get(uuid);
    }

    @NotNull
    public Set<IPossessedLand> getLands(World w) {
        if (indexWorld.get(w) == null) {
            return new HashSet<>();
        }
        return indexWorld.get(w);
    }

    @NotNull
    public Set<IPossessedLand> getLands() {
        if (indexLandname.size() == 0) {
            return Sets.newHashSet();
        }
        return new HashSet<>(indexLandname.values());
    }

    public boolean contains(String name) {
        return indexLandname.containsKey(name);
    }

    public void remove(String name) {
        IPossessedLand toRemove = indexLandname.get(name);

        indexPlayer.get(toRemove.getOwner()).remove(toRemove);
        indexWorld.get(toRemove.getWorld()).remove(toRemove);
        indexLandname.remove(name);
    }
}
