package biz.princeps.landlord.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum Mobs {

    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG),
    STRAY(EntityType.STRAY, Material.STRAY_SPAWN_EGG),
    HUSK(EntityType.HUSK, Material.HUSK_SPAWN_EGG),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, Material.ZOMBIE_VILLAGER_SPAWN_EGG),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, Material.ZOMBIE_HORSE_SPAWN_EGG),
    DONKEY(EntityType.DONKEY, Material.DONKEY_SPAWN_EGG),
    MULE(EntityType.MULE, Material.MULE_SPAWN_EGG),
    EVOKER(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG),
    VEX(EntityType.VEX, Material.VEX_SPAWN_EGG),
    VINDICATOR(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG),
    CREEPER(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG),
    SKELETON(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG),
    SPIDER(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG),
    ZOMBIE(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG),
    SLIME(EntityType.SLIME, Material.SLIME_SPAWN_EGG),
    GHAST(EntityType.GHAST, Material.GHAST_SPAWN_EGG),
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, Material.ZOMBIE_PIGMAN_SPAWN_EGG),
    ENDERMAN(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG),
    SILVERFISH(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG),
    BLAZE(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG),
    BAT(EntityType.BAT, Material.BAT_SPAWN_EGG),
    WITCH(EntityType.WITCH, Material.WITCH_SPAWN_EGG),
    GUARDIAN(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG),
    PIG(EntityType.PIG, Material.PIG_SPAWN_EGG),
    SHEEP(EntityType.SHEEP, Material.SHEEP_SPAWN_EGG),
    COW(EntityType.COW, Material.COW_SPAWN_EGG),
    CHICKEN(EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG),
    SQUID(EntityType.SQUID, Material.SQUID_SPAWN_EGG),
    WOLF(EntityType.WOLF, Material.WOLF_SPAWN_EGG),
    OCELOT(EntityType.OCELOT, Material.OCELOT_SPAWN_EGG),
    HORSE(EntityType.HORSE, Material.HORSE_SPAWN_EGG),
    RABBIT(EntityType.RABBIT, Material.RABBIT_SPAWN_EGG),
    POLAR_BEAR(EntityType.POLAR_BEAR, Material.POLAR_BEAR_SPAWN_EGG),
    LLAMA(EntityType.LLAMA, Material.LLAMA_SPAWN_EGG),
    PARROT(EntityType.PARROT, Material.PARROT_SPAWN_EGG),
    VILLAGER(EntityType.VILLAGER, Material.VILLAGER_SPAWN_EGG),
    TURTLE(EntityType.TURTLE, Material.TURTLE_SPAWN_EGG),
    PHANTOM(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG),
    COD(EntityType.COD, Material.COD_SPAWN_EGG),
    SALMON(EntityType.SALMON, Material.SALMON_SPAWN_EGG),
    PUFFERFISH(EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, Material.TROPICAL_FISH_SPAWN_EGG),
    DROWNED(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG),
    DOLPHIN(EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG);

    EntityType t;
    Material egg;

    Mobs(EntityType t, Material egg) {
        this.t = t;
        this.egg = egg;
    }

    public EntityType getType() {
        return t;
    }

    public Material getEgg() {
        return egg;
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

    public com.sk89q.worldedit.world.entity.EntityType getWGType() {
        return com.sk89q.worldedit.world.entity.EntityType.REGISTRY.get(t.getName());
    }
}
