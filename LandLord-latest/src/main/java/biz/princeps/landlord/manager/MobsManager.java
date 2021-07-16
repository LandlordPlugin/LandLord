package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IMobManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MobsManager implements IMobManager {

    private static final List<IMob> MOBS = new ArrayList<>();
    final int currentDataVersion;

    public MobsManager() {
        // https://minecraft.fandom.com/wiki/Data_version
        currentDataVersion = Bukkit.getUnsafe().getDataVersion();

        registerDefaultEntities();

        if (currentDataVersion >= 1952) {
            register1_14Entities();
        }

        if (currentDataVersion >= 2225) {
            register1_15Entities();
        }

        if (currentDataVersion >= 2566) {
            register1_16Entities();
        }

        if (currentDataVersion >= 2578) {
            register1_16_2Entities();
        }

        if (currentDataVersion >= 2724) {
            register1_17Entities();
        }

        MOBS.sort(Comparator.comparing(iMob -> iMob.getType().name()));
    }

    private void registerDefaultEntities() {
        // Default entities (>= 1.13 currently)
        Mob ELDER_GUARDIAN = new Mob(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG);
        Mob WITHER_SKELETON = new Mob(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG);
        Mob STRAY = new Mob(EntityType.STRAY, Material.STRAY_SPAWN_EGG);
        Mob HUSK = new Mob(EntityType.HUSK, Material.HUSK_SPAWN_EGG);
        Mob ZOMBIE_VILLAGER = new Mob(EntityType.ZOMBIE_VILLAGER, Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        Mob SKELETON_HORSE = new Mob(EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG);
        Mob ZOMBIE_HORSE = new Mob(EntityType.ZOMBIE_HORSE, Material.ZOMBIE_HORSE_SPAWN_EGG);
        Mob DONKEY = new Mob(EntityType.DONKEY, Material.DONKEY_SPAWN_EGG);
        Mob MULE = new Mob(EntityType.MULE, Material.MULE_SPAWN_EGG);
        Mob EVOKER = new Mob(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG);
        Mob VEX = new Mob(EntityType.VEX, Material.VEX_SPAWN_EGG);
        Mob VINDICATOR = new Mob(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG);
        Mob CREEPER = new Mob(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG);
        Mob SKELETON = new Mob(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG);
        Mob SPIDER = new Mob(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG);
        Mob ZOMBIE = new Mob(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG);
        Mob SLIME = new Mob(EntityType.SLIME, Material.SLIME_SPAWN_EGG);
        Mob GHAST = new Mob(EntityType.GHAST, Material.GHAST_SPAWN_EGG);
        Mob ENDERMAN = new Mob(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG);
        Mob CAVE_SPIDER = new Mob(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG);
        Mob SILVERFISH = new Mob(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG);
        Mob BLAZE = new Mob(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG);
        Mob MAGMA_CUBE = new Mob(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG);
        Mob BAT = new Mob(EntityType.BAT, Material.BAT_SPAWN_EGG);
        Mob WITCH = new Mob(EntityType.WITCH, Material.WITCH_SPAWN_EGG);
        Mob GUARDIAN = new Mob(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG);
        Mob PIG = new Mob(EntityType.PIG, Material.PIG_SPAWN_EGG);
        Mob SHEEP = new Mob(EntityType.SHEEP, Material.SHEEP_SPAWN_EGG);
        Mob COW = new Mob(EntityType.COW, Material.COW_SPAWN_EGG);
        Mob CHICKEN = new Mob(EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG);
        Mob SQUID = new Mob(EntityType.SQUID, Material.SQUID_SPAWN_EGG);
        Mob WOLF = new Mob(EntityType.WOLF, Material.WOLF_SPAWN_EGG);
        Mob OCELOT = new Mob(EntityType.OCELOT, Material.OCELOT_SPAWN_EGG);
        Mob HORSE = new Mob(EntityType.HORSE, Material.HORSE_SPAWN_EGG);
        Mob RABBIT = new Mob(EntityType.RABBIT, Material.RABBIT_SPAWN_EGG);
        Mob POLAR_BEAR = new Mob(EntityType.POLAR_BEAR, Material.POLAR_BEAR_SPAWN_EGG);
        Mob LLAMA = new Mob(EntityType.LLAMA, Material.LLAMA_SPAWN_EGG);
        Mob PARROT = new Mob(EntityType.PARROT, Material.PARROT_SPAWN_EGG);
        Mob VILLAGER = new Mob(EntityType.VILLAGER, Material.VILLAGER_SPAWN_EGG);
        Mob TURTLE = new Mob(EntityType.TURTLE, Material.TURTLE_SPAWN_EGG);
        Mob PHANTOM = new Mob(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG);
        Mob COD = new Mob(EntityType.COD, Material.COD_SPAWN_EGG);
        Mob SALMON = new Mob(EntityType.SALMON, Material.SALMON_SPAWN_EGG);
        Mob PUFFERFISH = new Mob(EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG);
        Mob TROPICAL_FISH = new Mob(EntityType.TROPICAL_FISH, Material.TROPICAL_FISH_SPAWN_EGG);
        Mob DROWNED = new Mob(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG);
        Mob DOLPHIN = new Mob(EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG);
        Mob MUSHROOM_COW = new Mob(EntityType.MUSHROOM_COW, Material.MOOSHROOM_SPAWN_EGG);

        // PIG_ZOMBIE still exists in 1.15.1-
        if (currentDataVersion <= 2227) {
            Mob PIG_ZOMBIE = new Mob(EntityType.valueOf("PIG_ZOMBIE"), Material.valueOf("ZOMBIE_PIGMAN_SPAWN_EGG"));
        }
    }

    private void register1_14Entities() {
        // 1.14's entities
        Mob CAT = new Mob(EntityType.CAT, Material.CAT_SPAWN_EGG);
        Mob TRADER_LLAMA = new Mob(EntityType.TRADER_LLAMA, Material.TRADER_LLAMA_SPAWN_EGG);
        Mob WANDERING_TRADER = new Mob(EntityType.WANDERING_TRADER, Material.WANDERING_TRADER_SPAWN_EGG);
        Mob PANDA = new Mob(EntityType.PANDA, Material.PANDA_SPAWN_EGG);
        Mob PILLAGER = new Mob(EntityType.PILLAGER, Material.PILLAGER_SPAWN_EGG);
        Mob RAVAGER = new Mob(EntityType.RAVAGER, Material.RAVAGER_SPAWN_EGG);
        Mob FOX = new Mob(EntityType.FOX, Material.FOX_SPAWN_EGG);
    }

    private void register1_15Entities() {
        // 1.15's entities
        Mob BEE = new Mob(EntityType.BEE, Material.BEE_SPAWN_EGG);
    }

    private void register1_16Entities() {
        // 1.16's entities
        Mob HOGLIN = new Mob(EntityType.HOGLIN, Material.HOGLIN_SPAWN_EGG);
        Mob PIGLIN = new Mob(EntityType.PIGLIN, Material.PIGLIN_SPAWN_EGG);
        Mob STRIDER = new Mob(EntityType.STRIDER, Material.STRIDER_SPAWN_EGG);
        Mob ZOGLIN = new Mob(EntityType.ZOGLIN, Material.ZOGLIN_SPAWN_EGG);
        Mob ZOMBIFIED_PIGLIN = new Mob(EntityType.ZOMBIFIED_PIGLIN, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
    }

    private void register1_16_2Entities() {
        // 1.16.2's entities
        Mob PIGLIN_BRUTE = new Mob(EntityType.PIGLIN_BRUTE, Material.PIGLIN_BRUTE_SPAWN_EGG);
    }

    private void register1_17Entities() {
        // 1.17's entities
        Mob AXOLOTL = new Mob(EntityType.AXOLOTL, Material.AXOLOTL_SPAWN_EGG);
        Mob GLOW_SQUID = new Mob(EntityType.GLOW_SQUID, Material.GLOW_SQUID_SPAWN_EGG);
        Mob GOAT = new Mob(EntityType.GOAT, Material.GOAT_SPAWN_EGG);
    }

    @Override
    public Collection<IMob> values() {
        return MOBS;
    }

    @Override
    public IMob get(EntityType type) {
        for (IMob mob : MOBS) {
            if (mob.getType().equals(type)) {
                return mob;
            }
        }
        return null;
    }

    @Override
    public IMob valueOf(String name) {
        for (IMob mob : MOBS) {
            if (mob.getName().equalsIgnoreCase(name)) {
                return mob;
            }
        }
        return null;
    }

    public static class Mob implements IMob {

        private final EntityType type;
        private final Material egg;
        private final String permission;

        Mob(EntityType type, Material egg) {
            this.type = type;
            this.egg = egg;
            this.permission = "landlord.player.manage.mob-spawning." + type.name();

            MOBS.add(this);
        }

        @Override
        public EntityType getType() {
            return type;
        }

        @Override
        public ItemStack getEgg() {
            return new ItemStack(egg);
        }

        @Override
        public String getPermission() {
            return permission;
        }

        @Override
        public String getNiceName() {
            String s = type.getName();
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

        @Override
        public String getName() {
            return type.getName().toUpperCase();
        }
    }
}
