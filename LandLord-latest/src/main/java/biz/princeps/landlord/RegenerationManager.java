package biz.princeps.landlord;

import biz.princeps.landlord.api.IRegenerationManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;

public class RegenerationManager implements IRegenerationManager {

    @Override
    public void regenerateChunk(World world, int x, int z) {
        Region region = new CuboidRegion(BlockVector3.at(x << 4, 0, z << 4),
                BlockVector3.at((x << 4) + 15, 255, (z << 4) + 15));
        WorldEdit worldEdit = WorldEdit.getInstance();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(weWorld, -1);
        weWorld.regenerate(region, editSession);
        editSession.close();
    }
}
