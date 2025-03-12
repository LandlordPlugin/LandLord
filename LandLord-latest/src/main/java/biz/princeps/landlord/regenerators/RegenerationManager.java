package biz.princeps.landlord.regenerators;

import biz.princeps.landlord.LandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IRegenerationManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class RegenerationManager implements IRegenerationManager {

    @Override
    public void regenerateChunk(World world, int x, int z) {
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        Region region = regionForChunk(x, z, weWorld.getMinY(), weWorld.getMaxY());
        WorldEdit worldEdit = WorldEdit.getInstance();
        EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(weWorld, -1);
        weWorld.regenerate(region, editSession);
        editSession.close();
    }

    @Override
    public CompletableFuture<Void> regenerateChunks(Iterable<IOwnedLand> lands) {
        List<Region> regions = new ArrayList<>();
        World world = null;
        for (IOwnedLand land : lands) {
            if (world == null) {
                world = land.getWorld();
            } else if (!world.equals(land.getWorld())) {
                return CompletableFuture.failedFuture(new IllegalArgumentException("Lands from different worlds"));
            }
            regions.add(regionForChunk(land.getChunkX(), land.getChunkZ(), world.getMinHeight(), world.getMaxHeight()));
        }
        RegionIntersection intersection = new RegionIntersection(regions);
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        if (isFawe()) {
            return CompletableFuture.runAsync(() -> regenerate(weWorld, intersection))
                .exceptionally(ex -> {
                    LandLord.getPlugin(LandLord.class).getLogger().log(Level.SEVERE, "failed to regenerate chunks", ex);
                    return null;
                });
        }
        regenerate(weWorld, intersection);
        return CompletableFuture.completedFuture(null);
    }

    private static boolean isFawe() {
        return WorldEditPlugin.getPlugin(WorldEditPlugin.class).getName().equals("FastAsyncWorldEdit");
    }

    private static void regenerate(com.sk89q.worldedit.world.World weWorld, RegionIntersection intersection) {
        try (EditSession es = WorldEdit.getInstance().newEditSession(weWorld)) {
            weWorld.regenerate(intersection, es);
        }
    }

    private static CuboidRegion regionForChunk(int x, int z, int minY, int maxY) {
        return new CuboidRegion(BlockVector3.at(x << 4, minY, z << 4),
            BlockVector3.at((x << 4) + 15, maxY, (z << 4) + 15));
    }
}
