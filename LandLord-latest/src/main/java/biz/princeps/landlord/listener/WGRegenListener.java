package biz.princeps.landlord.listener;

import biz.princeps.landlord.api.ClaimType;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.events.LandPostClaimEvent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This listener creates a snapshot of a chunk after the claim process was done successfully.
 */
public class WGRegenListener extends BasicListener {

    public WGRegenListener(ILandLord plugin) {
        super(plugin);
    }

    @EventHandler
    public void onClaim(LandPostClaimEvent e) {

        // We want to regenerate it to the state where it was claimed initially
        if (e.getClaimType() != ClaimType.FREE_LAND) {
            return;
        }

        int x = e.getLand().getChunkX();
        int z = e.getLand().getChunkZ();
        World world = e.getLand().getWorld();

        Region region = new CuboidRegion(BlockVector3.at(x << 4, 0, z << 4),
                BlockVector3.at((x << 4) + 15, 255, (z << 4) + 15));

        WorldEdit worldEdit = WorldEdit.getInstance();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(weWorld, -1);

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(region.getMinimumPoint());
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(true);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException worldEditException) {
            worldEditException.printStackTrace();
        }

        File file = new File(new File(plugin.getPlugin().getDataFolder(), "chunksaves"), e.getLand().getName());

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        editSession.close();
    }
}
