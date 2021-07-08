package biz.princeps.lib.crossversion;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MaterialProxy {

    CLOCK("CLOCK", "WATCH", (byte) 0),
    GREEN_WOOL("GREEN_WOOL", "WOOL", (byte) 5),
    RED_WOOL("RED_WOOL", "WOOL", (byte) 14);

    final String latest;
    final String legacy;
    final byte legacybyte;

    MaterialProxy(String latest, String legacy, byte legacybyte) {
        this.latest = latest;
        this.legacy = legacy;
        this.legacybyte = legacybyte;
    }

    public ItemStack crossVersion() {
        String version = CrossVersion.getVersion();
        if ("v1_12_R1".equals(version)) {
            return getLegacy();
        }
        return getLatest();
    }

    ItemStack getLatest() {
        return new ItemStack(Material.valueOf(latest));
    }

    ItemStack getLegacy() {
        return new ItemStack(Material.valueOf(legacy), 1, legacybyte);

    }

}
