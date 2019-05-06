package biz.princeps.landlord.listener;

import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.util.Materials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 05/05/19
 * <p>
 * This listener overwrites worldguard events in order to allow pistons to move blocks between regions
 * that are owned by the same person.
 */
public class PistonOverwriter extends BasicListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceBlock(final PlaceBlockEvent event) {
        Block block = event.getCause().getFirstBlock();
        if (block != null) {
            if (Materials.isPistonBlock(block.getType()) || block.getType() == Material.MOVING_PISTON) {
                if (sameOwner(block, event.getBlocks())) {
                    event.setAllowed(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreakBlock(final BreakBlockEvent event) {
        Block block = event.getCause().getFirstBlock();
        if (block != null) {
            if (Materials.isPistonBlock(block.getType()) || block.getType() == Material.MOVING_PISTON) {
                if (sameOwner(block, event.getBlocks())) {
                    event.setAllowed(true);
                }
            }
        }
    }

    /**
     * Checks if all the blocks in the list are on the same land as the origin block
     *
     * @param origin the origin (the piston)
     * @param blocks all the pushed blocks
     * @return if all blocks have the same owner
     */
    private boolean sameOwner(Block origin, List<Block> blocks) {
        Set<OwnedLand> lands = new HashSet<>();
        blocks.forEach(b -> lands.add(plugin.getLand(b.getChunk())));

        UUID onlyOwner = plugin.getLand(origin.getChunk()).getOwner();
        // System.out.println("original owner" + onlyOwner);
        for (OwnedLand land : lands) {
            // System.out.println(land);
            if (land == null) {
                return false;
            }
            if (!onlyOwner.equals(land.getOwner())) {
                return false;
            }
        }

        return true;
    }
}
