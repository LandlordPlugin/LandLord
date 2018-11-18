package biz.princeps.landlord.util;

import biz.princeps.landlord.Landlord;
import com.sk89q.worldedit.math.BlockVector2;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
    private static Map<BlockVector2, Material> PATTERN;

    /**
     * Returns the delimitation pattern defined in the config in a way, the plugin can work with
     *   x --------->
     * z mmmmmmmmmmmmmmmm
     * | m--------------m
     * | m--------------m
     * | ...
     * |
     * v
     *
     * @return a map of a vector and a material
     */
    public static Map<BlockVector2, Material> getDelimitationPattern() {
        if (PATTERN != null) {
            return PATTERN;
        }

        List<String> cfgString = plugin.getConfig().getStringList("CommandSettings.Claim.delimitation");
        Map<Character, Material> varToMaterial = new HashMap<>();
        Map<BlockVector2, Material> delimitPattern = new HashMap<>();

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
                    delimitPattern.put(BlockVector2.at(x, z), material);
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


    public static void delimit(Chunk chunk) {
        Map<BlockVector2, Material> pattern = getDelimitationPattern();
        if (pattern == null) {
            plugin.getLogger().warning("Delimitation failed, because there was an error in the config!");
            return;
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Material mat = pattern.get(BlockVector2.at(x, z));

                if (mat != null) {
                    int highestY = chunk.getWorld().getHighestBlockYAt(chunk.getX() * 16 + x, chunk.getZ() * 16 + z);
                    Block b = chunk.getBlock(x, highestY, z);
                    b.setType(mat);
                }
            }
        }
    }


}
