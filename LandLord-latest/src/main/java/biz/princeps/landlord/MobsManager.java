package biz.princeps.landlord;

import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IMobManager;
import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class MobsManager implements IMobManager {

    private static Collection<IMob> MOBS = new ArrayList<>();

    public MobsManager() {
        // Based on protocol version got with ProtocolLib
        final int currentProtocolVersion = MinecraftProtocolVersion.getCurrentVersion();

        registerDefaultEntities();

        if (currentProtocolVersion >= 477) {
            register1_14Entities();
        }

        if (currentProtocolVersion >= 573) {
            register1_15Entities();
        }
    }

    private void registerDefaultEntities() {
        // Default entities (>= 1.13 currently)
        final Mob ELDER_GUARDIAN = new Mob(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG);
        final Mob WITHER_SKELETON = new Mob(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG);
        final Mob STRAY = new Mob(EntityType.STRAY, Material.STRAY_SPAWN_EGG);
        final Mob HUSK = new Mob(EntityType.HUSK, Material.HUSK_SPAWN_EGG);
        final Mob ZOMBIE_VILLAGER = new Mob(EntityType.ZOMBIE_VILLAGER, Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        final Mob SKELETON_HORSE = new Mob(EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG);
        final Mob ZOMBIE_HORSE = new Mob(EntityType.ZOMBIE_HORSE, Material.ZOMBIE_HORSE_SPAWN_EGG);
        final Mob DONKEY = new Mob(EntityType.DONKEY, Material.DONKEY_SPAWN_EGG);
        final Mob MULE = new Mob(EntityType.MULE, Material.MULE_SPAWN_EGG);
        final Mob EVOKER = new Mob(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG);
        final Mob VEX = new Mob(EntityType.VEX, Material.VEX_SPAWN_EGG);
        final Mob VINDICATOR = new Mob(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG);
        final Mob CREEPER = new Mob(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG);
        final Mob SKELETON = new Mob(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG);
        final Mob SPIDER = new Mob(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG);
        final Mob ZOMBIE = new Mob(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG);
        final Mob SLIME = new Mob(EntityType.SLIME, Material.SLIME_SPAWN_EGG);
        final Mob GHAST = new Mob(EntityType.GHAST, Material.GHAST_SPAWN_EGG);
        final Mob PIG_ZOMBIE = new Mob(EntityType.PIG_ZOMBIE, Material.ZOMBIE_PIGMAN_SPAWN_EGG);
        final Mob ENDERMAN = new Mob(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG);
        final Mob CAVE_SPIDER = new Mob(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG);
        final Mob SILVERFISH = new Mob(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG);
        final Mob BLAZE = new Mob(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG);
        final Mob MAGMA_CUBE = new Mob(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG);
        final Mob BAT = new Mob(EntityType.BAT, Material.BAT_SPAWN_EGG);
        final Mob WITCH = new Mob(EntityType.WITCH, Material.WITCH_SPAWN_EGG);
        final Mob GUARDIAN = new Mob(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG);
        final Mob PIG = new Mob(EntityType.PIG, Material.PIG_SPAWN_EGG);
        final Mob SHEEP = new Mob(EntityType.SHEEP, Material.SHEEP_SPAWN_EGG);
        final Mob COW = new Mob(EntityType.COW, Material.COW_SPAWN_EGG);
        final Mob CHICKEN = new Mob(EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG);
        final Mob SQUID = new Mob(EntityType.SQUID, Material.SQUID_SPAWN_EGG);
        final Mob WOLF = new Mob(EntityType.WOLF, Material.WOLF_SPAWN_EGG);
        final Mob OCELOT = new Mob(EntityType.OCELOT, Material.OCELOT_SPAWN_EGG);
        final Mob HORSE = new Mob(EntityType.HORSE, Material.HORSE_SPAWN_EGG);
        final Mob RABBIT = new Mob(EntityType.RABBIT, Material.RABBIT_SPAWN_EGG);
        final Mob POLAR_BEAR = new Mob(EntityType.POLAR_BEAR, Material.POLAR_BEAR_SPAWN_EGG);
        final Mob LLAMA = new Mob(EntityType.LLAMA, Material.LLAMA_SPAWN_EGG);
        final Mob PARROT = new Mob(EntityType.PARROT, Material.PARROT_SPAWN_EGG);
        final Mob VILLAGER = new Mob(EntityType.VILLAGER, Material.VILLAGER_SPAWN_EGG);
        final Mob TURTLE = new Mob(EntityType.TURTLE, Material.TURTLE_SPAWN_EGG);
        final Mob PHANTOM = new Mob(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG);
        final Mob COD = new Mob(EntityType.COD, Material.COD_SPAWN_EGG);
        final Mob SALMON = new Mob(EntityType.SALMON, Material.SALMON_SPAWN_EGG);
        final Mob PUFFERFISH = new Mob(EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG);
        final Mob TROPICAL_FISH = new Mob(EntityType.TROPICAL_FISH, Material.TROPICAL_FISH_SPAWN_EGG);
        final Mob DROWNED = new Mob(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG);
        final Mob DOLPHIN = new Mob(EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG);
        final Mob MUSHROOM_COW = new Mob(EntityType.MUSHROOM_COW, Material.MOOSHROOM_SPAWN_EGG);
    }

    private void register1_14Entities() {
        // 1.14's entities
        final Mob CAT = new Mob(EntityType.CAT, Material.CAT_SPAWN_EGG);
        final Mob TRADER_LLAMA = new Mob(EntityType.TRADER_LLAMA, Material.TRADER_LLAMA_SPAWN_EGG);
        final Mob WANDERING_TRADER = new Mob(EntityType.WANDERING_TRADER, Material.WANDERING_TRADER_SPAWN_EGG);
        final Mob PANDA = new Mob(EntityType.PANDA, Material.PANDA_SPAWN_EGG);
        final Mob PILLAGER = new Mob(EntityType.PILLAGER, Material.PILLAGER_SPAWN_EGG);
        final Mob RAVAGER = new Mob(EntityType.RAVAGER, Material.RAVAGER_SPAWN_EGG);
        final Mob FOX = new Mob(EntityType.FOX, Material.FOX_SPAWN_EGG);
    }

    private void register1_15Entities() {
        // 1.15's entities
        final Mob BEE = new Mob(EntityType.BEE, Material.BEE_SPAWN_EGG);
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

        EntityType t;
        Material egg;

        Mob(EntityType t, Material egg) {
            this.t = t;
            this.egg = egg;

            MOBS.add(this);
        }

        @Override
        public EntityType getType() {
            return t;
        }

        @Override
        public ItemStack getEgg() {
            return new ItemStack(egg);
        }

        @Override
        public String getNiceName() {
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

        @Override
        public String getName() {
            return t.getName().toUpperCase();
        }
    }
}
