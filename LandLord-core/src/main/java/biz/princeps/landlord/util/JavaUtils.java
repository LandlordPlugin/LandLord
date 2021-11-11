package biz.princeps.landlord.util;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.ILangManager;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

public class JavaUtils {

    public static final BlockFace[] BLOCK_FACES = new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Checks if the player is in a disabled world and send him a message if he is.
     *
     * @return if the player is in a disabled world.
     */
    public static boolean isDisabledWorld(ILangManager lm, ILandLord plugin, Player player, boolean message) {
        return isDisabledWorld(lm, plugin, player, player.getWorld(), message);
    }

    /**
     * Checks if the world is disabled and sends a message to the player if the world is disabled.
     *
     * @param player the player to send the message
     * @param world  the world to check if its disabled
     * @return if the world is disabled
     */
    public static boolean isDisabledWorld(ILangManager lm, ILandLord plugin, Player player, World world, boolean message) {
        List<String> stringList = plugin.getConfig().getStringList("disabled-worlds");

        for (String s : stringList) {
            if (Pattern.compile(s).matcher(world.getName()).matches()) {
                if (message)
                    lm.sendMessage(player, lm.getString(player, "Disabled-World"));
                return true;
            }
        }
        return false;
    }

    /**
     * Original link : https://github.com/bergerhealer/BKCommonLib/blob/master/src/main/java/com/bergerkiller/bukkit/common/utils/FaceUtil.java
     * <p>
     * Gets the horizontal Block Face from a given yaw angle
     *
     * @param yaw angle
     * @return The Block Face of the angle
     */
    public static BlockFace getBlockFace(float yaw) {
        return BLOCK_FACES[(Math.round(yaw / 90f) & 0x3)];
    }

}
