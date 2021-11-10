package biz.princeps.landlord.regenerators;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IRegenerationManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WGRegenerator implements IRegenerationManager {

    private final ILandLord plugin;

    public WGRegenerator(ILandLord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void regenerateChunk(World world, int x, int z) {

        WorldEdit worldEdit = WorldEdit.getInstance();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        Chunk chunk = world.getChunkAt(x, z);
        String landName = plugin.getWGManager().getLandName(chunk);

        // heal all players so that they dont suffocate in case they have only half a heart left. later we port them up
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.PLAYER) {
                plugin.getPlugin().getServer().getPlayer(entity.getName()).setHealth(20);
            }
        }

        File file = new File(new File(plugin.getPlugin().getDataFolder(), "chunksaves"), landName);

        if (file.exists()) {
            ClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;

            try (EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(weWorld, -1);
                 ClipboardReader reader = format.getReader(new FileInputStream(file))) {

                Clipboard clipboard = reader.read();
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(x << 4, 0, z << 4))
                        .ignoreAirBlocks(false)
                        .build();

                Operations.complete(operation);

            } catch (IOException | WorldEditException e) {
                e.printStackTrace();
            }
        }

        // Teleport players up so that they dont suffocate.
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.PLAYER) {
                Player p = plugin.getPlugin().getServer().getPlayer(entity.getName());
                p.setHealth(20);
                p.teleport(world.getHighestBlockAt(p.getLocation().add(0, 3, 0)).getLocation());
            }
        }
    }
}
