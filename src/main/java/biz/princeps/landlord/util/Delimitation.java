package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import com.sk89q.worldedit.BlockVector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 18/11/18
 */
public class Delimitation {

    private static Landlord plugin = Landlord.getInstance();
    private static Map<BlockVector, Material> PATTERN;

    /**
     * Returns the delimitation pattern defined in the config in a way, the plugin can work with
     * x --------->
     * z mmmmmmmmmmmmmmmm
     * | m--------------m
     * | m--------------m
     * | ...
     * |
     * v
     *
     * @return a map of a vector and a material
     */
    public static Map<BlockVector, Material> getDelimitationPattern() {
        if (PATTERN != null) {
            return PATTERN;
        }

        List<String> cfgString = plugin.getConfig().getStringList("CommandSettings.Claim.delimitation");
        Map<Character, Material> varToMaterial = new HashMap<>();
        Map<BlockVector, Material> delimitPattern = new HashMap<>();

        int x = 0;
        for (String s : cfgString) {
            // its a variable definition
            if (s.startsWith("define:")) {
                String a = s.split(":")[1].trim();
                char var = a.split("=")[0].charAt(0);
                Material mat = Material.getMaterial(a.split("=")[1]);
                if (mat == null) {
                    plugin.getLogger().warning("Invalid Material in delimitation!");
                    return null;
                }
                varToMaterial.put(var, mat);
            } else if (s.length() == 16) {
                // must be a String containing 16chars describing the pattern
                for (int z = 0; z < 16; z++) {
                    char varString = s.charAt(z);
                    Material material = varToMaterial.get(varString);
                    delimitPattern.put(new BlockVector(x, 0, z), material);
                }
                x++;
            } else {
                plugin.getLogger().warning("Invalid line '" + s + "' detected!!");
                return null;
            }
        }
        PATTERN = delimitPattern;
        return delimitPattern;
    }


    public static void delimit(Player player, Chunk chunk) {
        Map<BlockVector, Material> pattern = getDelimitationPattern();
        if (pattern == null) {
            plugin.getLogger().warning("Delimitation failed, because there was an error in the config!");
            return;
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Material mat = pattern.get(new BlockVector(x, 0, z));

                if (mat != null) {
                    int highestY = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
                    Block b = chunk.getBlock(x, highestY, z);

                    while (b.getType() != Material.AIR) {
                        b = chunk.getBlock(x, ++highestY, z);
                    }

                    if (plugin.getConfig().getBoolean("CommandSettings.Claim.enablePhantomBlocks")) {
                        sendBlockChangePacket(player, b.getLocation(), mat);
                    } else {
                        b.setType(mat);
                    }
                }
            }
        }
    }

    private static void sendBlockChangePacket(Player p, Location loc, Material mat) {
        PacketContainer fakeblock = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        fakeblock.getBlockPositionModifier().write(0, new BlockPosition(
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        fakeblock.getBlockData().write(0, WrappedBlockData.createData(mat));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, fakeblock);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + fakeblock, e);
        }
    }
}
