package biz.princeps.landlord.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public enum Mobs {

    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
    WITHER_SKELETON(EntityType.WITHER_SKELETON),
    STRAY(EntityType.STRAY),
    HUSK(EntityType.HUSK),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER),
    SKELETON_HORSE(EntityType.SKELETON_HORSE),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE),
    DONKEY(EntityType.DONKEY),
    MULE(EntityType.MULE),
    EVOKER(EntityType.EVOKER),
    VEX(EntityType.VEX),
    VINDICATOR(EntityType.VINDICATOR),
    CREEPER(EntityType.CREEPER),
    SKELETON(EntityType.SKELETON),
    SPIDER(EntityType.SPIDER),
    ZOMBIE(EntityType.ZOMBIE),
    SLIME(EntityType.SLIME),
    GHAST(EntityType.GHAST),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE),
    ENDERMAN(EntityType.ENDERMAN),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    SILVERFISH(EntityType.SILVERFISH),
    BLAZE(EntityType.BLAZE),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    BAT(EntityType.BAT),
    WITCH(EntityType.WITCH),
    GUARDIAN(EntityType.GUARDIAN),
    PIG(EntityType.PIG),
    SHEEP(EntityType.SHEEP),
    COW(EntityType.COW),
    CHICKEN(EntityType.CHICKEN),
    SQUID(EntityType.SQUID),
    WOLF(EntityType.WOLF),
    OCELOT(EntityType.OCELOT),
    HORSE(EntityType.HORSE),
    RABBIT(EntityType.RABBIT),
    POLAR_BEAR(EntityType.POLAR_BEAR),
    LLAMA(EntityType.LLAMA),
    PARROT(EntityType.PARROT),
    VILLAGER(EntityType.VILLAGER);

    EntityType t;

    Mobs(EntityType t) {
        this.t = t;
    }

    public EntityType getType() {
        return t;
    }

    public ItemStack getEgg() {
        ItemStack eggstack  = new ItemStack(Material.MONSTER_EGG);
        SpawnEggMeta itemMeta = (SpawnEggMeta) eggstack.getItemMeta();
        itemMeta.setSpawnedType(t);
        eggstack.setItemMeta(itemMeta);
        return eggstack;
    }

    public String getName() {
        String s = t.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(s.charAt(0)));
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                sb.append(' ');
                sb.append(Character.toUpperCase(s.charAt(++i)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
