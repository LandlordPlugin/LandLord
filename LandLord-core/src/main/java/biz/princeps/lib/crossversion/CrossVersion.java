package biz.princeps.lib.crossversion;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Created by spatium on 28.07.17.
 */
public class CrossVersion {

    private final IItem item;
    private MaterialProxy materialProxy;

    public CrossVersion() {
        String version = getVersion();

        item = new Item();
        /*
        switch (version) {
            default:
                PrincepsLib.getPluginInstance().getLogger().warning("Invalid minecraft version! This version is not supported");
                PrincepsLib.getPluginInstance().getPluginLoader().disablePlugin(PrincepsLib.getPluginInstance());
                break;
            case "v1_12_R1":

            case "v1_13_R2":
                item = new biz.princeps.lib.crossversion.v1_13_R2.Item();
                break;
        }
        */

    }

    public ItemStack addNBTTag(ItemStack stack, String key, Object value) {
        return item.addNBTTag(stack, key, value);
    }

    public Object getValueFromNBT(ItemStack stack, String key) {
        return item.getValueFromNBT(stack, key);
    }


    public boolean hasNBTTag(ItemStack stack, String customItem) {
        return item.hasNBTTag(stack, customItem);
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }
}
