package biz.princeps.landlord.manager;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IMobManager;
import biz.princeps.landlord.util.Skulls;
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

    private final ILandLord plugin;

    public MobsManager(ILandLord plugin) {
        this.plugin = plugin;
        // https://minecraft.fandom.com/wiki/Data_version
        this.currentDataVersion = plugin.getServer().getUnsafe().getDataVersion();

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

        if (currentDataVersion >= 3105) {
            register1_19Entities();
        }

        MOBS.sort(Comparator.comparing(iMob -> iMob.getType().name()));
    }

    private void registerDefaultEntities() {
        // Default entities (>= 1.13 currently)
        Mob ELDER_GUARDIAN = new Mob(EntityType.ELDER_GUARDIAN, Skulls.ELDER_GUARDIAN.getSkull(plugin));
        Mob WITHER_SKELETON = new Mob(EntityType.WITHER_SKELETON, new ItemStack(Material.WITHER_SKELETON_SKULL));
        Mob STRAY = new Mob(EntityType.STRAY, Skulls.STRAY.getSkull(plugin));
        Mob HUSK = new Mob(EntityType.HUSK, Skulls.HUSK.getSkull(plugin));
        Mob ZOMBIE_VILLAGER = new Mob(EntityType.ZOMBIE_VILLAGER, Skulls.ZOMBIE_VILLAGER.getSkull(plugin));
        Mob SKELETON_HORSE = new Mob(EntityType.SKELETON_HORSE, Skulls.SKELETON_HORSE.getSkull(plugin));
        Mob ZOMBIE_HORSE = new Mob(EntityType.ZOMBIE_HORSE, Skulls.ZOMBIE_HORSE.getSkull(plugin));
        Mob DONKEY = new Mob(EntityType.DONKEY, Skulls.DONKEY.getSkull(plugin));
        Mob MULE = new Mob(EntityType.MULE, Skulls.MULE.getSkull(plugin));
        Mob EVOKER = new Mob(EntityType.EVOKER, Skulls.EVOKER.getSkull(plugin));
        Mob VEX = new Mob(EntityType.VEX, Skulls.VEX.getSkull(plugin));
        Mob VINDICATOR = new Mob(EntityType.VINDICATOR, Skulls.VINDICATOR.getSkull(plugin));
        Mob CREEPER = new Mob(EntityType.CREEPER, new ItemStack(Material.CREEPER_HEAD));
        Mob SKELETON = new Mob(EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL));
        Mob SPIDER = new Mob(EntityType.SPIDER, Skulls.SPIDER.getSkull(plugin));
        Mob ZOMBIE = new Mob(EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD));
        Mob SLIME = new Mob(EntityType.SLIME, Skulls.SLIME.getSkull(plugin));
        Mob GHAST = new Mob(EntityType.GHAST, Skulls.GHAST.getSkull(plugin));
        Mob ENDERMAN = new Mob(EntityType.ENDERMAN, Skulls.ENDERMAN.getSkull(plugin));
        Mob CAVE_SPIDER = new Mob(EntityType.CAVE_SPIDER, Skulls.CAVE_SPIDER.getSkull(plugin));
        Mob SILVERFISH = new Mob(EntityType.SILVERFISH, Skulls.SILVERFISH.getSkull(plugin));
        Mob BLAZE = new Mob(EntityType.BLAZE, Skulls.BLAZE.getSkull(plugin));
        Mob MAGMA_CUBE = new Mob(EntityType.MAGMA_CUBE, Skulls.MAGMA_CUBE.getSkull(plugin));
        Mob BAT = new Mob(EntityType.BAT, Skulls.BAT.getSkull(plugin));
        Mob WITCH = new Mob(EntityType.WITCH, Skulls.WITCH.getSkull(plugin));
        Mob GUARDIAN = new Mob(EntityType.GUARDIAN, Skulls.GUARDIAN.getSkull(plugin));
        Mob PIG = new Mob(EntityType.PIG, Skulls.PIG.getSkull(plugin));
        Mob SHEEP = new Mob(EntityType.SHEEP, Skulls.WHITE_SHEEP.getSkull(plugin));
        Mob COW = new Mob(EntityType.COW, Skulls.COW.getSkull(plugin));
        Mob CHICKEN = new Mob(EntityType.CHICKEN, Skulls.CHICKEN.getSkull(plugin));
        Mob SQUID = new Mob(EntityType.SQUID, Skulls.SQUID.getSkull(plugin));
        Mob WOLF = new Mob(EntityType.WOLF, Skulls.WOLF.getSkull(plugin));
        Mob OCELOT = new Mob(EntityType.OCELOT, Skulls.OCELOT.getSkull(plugin));
        Mob HORSE = new Mob(EntityType.HORSE, Skulls.WHITE_HORSE.getSkull(plugin));
        Mob RABBIT = new Mob(EntityType.RABBIT, Skulls.BROWN_RABBIT.getSkull(plugin));
        Mob POLAR_BEAR = new Mob(EntityType.POLAR_BEAR, Skulls.POLAR_BEAR.getSkull(plugin));
        Mob LLAMA = new Mob(EntityType.LLAMA, Skulls.CREAMY_LLAMA.getSkull(plugin));
        Mob PARROT = new Mob(EntityType.PARROT, Skulls.RED_PARROT.getSkull(plugin));
        Mob VILLAGER = new Mob(EntityType.VILLAGER, Skulls.UNEMPLOYED_VILLAGER.getSkull(plugin));
        Mob TURTLE = new Mob(EntityType.TURTLE, Skulls.TURTLE.getSkull(plugin));
        Mob PHANTOM = new Mob(EntityType.PHANTOM, Skulls.PHANTOM.getSkull(plugin));
        Mob COD = new Mob(EntityType.COD, Skulls.COD.getSkull(plugin));
        Mob SALMON = new Mob(EntityType.SALMON, Skulls.SALMON.getSkull(plugin));
        Mob PUFFERFISH = new Mob(EntityType.PUFFERFISH, Skulls.PUFFERFISH.getSkull(plugin));
        Mob TROPICAL_FISH = new Mob(EntityType.TROPICAL_FISH, Skulls.TROPICAL_FISH.getSkull(plugin));
        Mob DROWNED = new Mob(EntityType.DROWNED, Skulls.DROWNED.getSkull(plugin));
        Mob DOLPHIN = new Mob(EntityType.DOLPHIN, Skulls.DOLPHIN.getSkull(plugin));
        Mob MUSHROOM_COW = new Mob(EntityType.MUSHROOM_COW, Skulls.RED_MOOSHROOM.getSkull(plugin));

        // PIG_ZOMBIE still exists in 1.15.1-
        if (currentDataVersion <= 2227) {
            Mob PIG_ZOMBIE = new Mob(EntityType.valueOf("PIG_ZOMBIE"), Skulls.ZOMBIFIED_PIGLIN.getSkull(plugin));
        }
    }

    private void register1_14Entities() {
        // 1.14's entities
        Mob CAT = new Mob(EntityType.CAT, Skulls.TABBY_CAT.getSkull(plugin));
        Mob TRADER_LLAMA = new Mob(EntityType.TRADER_LLAMA, Skulls.CREAMY_TRADER_LLAMA.getSkull(plugin));
        Mob WANDERING_TRADER = new Mob(EntityType.WANDERING_TRADER, Skulls.WANDERING_TRADER.getSkull(plugin));
        Mob PANDA = new Mob(EntityType.PANDA, Skulls.PANDA.getSkull(plugin));
        Mob PILLAGER = new Mob(EntityType.PILLAGER, Skulls.PILLAGER.getSkull(plugin));
        Mob RAVAGER = new Mob(EntityType.RAVAGER, Skulls.RAVAGER.getSkull(plugin));
        Mob FOX = new Mob(EntityType.FOX, Skulls.FOX.getSkull(plugin));
    }

    private void register1_15Entities() {
        // 1.15's entities
        Mob BEE = new Mob(EntityType.BEE, Skulls.BEE.getSkull(plugin));
    }

    private void register1_16Entities() {
        // 1.16's entities
        Mob HOGLIN = new Mob(EntityType.HOGLIN, Skulls.HOGLIN.getSkull(plugin));
        Mob PIGLIN = new Mob(EntityType.PIGLIN, Skulls.PIGLIN.getSkull(plugin));
        Mob STRIDER = new Mob(EntityType.STRIDER, Skulls.STRIDER.getSkull(plugin));
        Mob ZOGLIN = new Mob(EntityType.ZOGLIN, Skulls.ZOGLIN.getSkull(plugin));
        Mob ZOMBIFIED_PIGLIN = new Mob(EntityType.ZOMBIFIED_PIGLIN, Skulls.ZOMBIFIED_PIGLIN.getSkull(plugin));
    }

    private void register1_16_2Entities() {
        // 1.16.2's entities
        Mob PIGLIN_BRUTE = new Mob(EntityType.PIGLIN_BRUTE, Skulls.PIGLIN_BRUTE.getSkull(plugin));
    }

    private void register1_17Entities() {
        // 1.17's entities
        Mob AXOLOTL = new Mob(EntityType.AXOLOTL, Skulls.BLUE_AXOLOTL.getSkull(plugin));
        Mob GLOW_SQUID = new Mob(EntityType.GLOW_SQUID, Skulls.GLOW_SQUID.getSkull(plugin));
        Mob GOAT = new Mob(EntityType.GOAT, Skulls.GOAT.getSkull(plugin));
    }

    private void register1_19Entities() {
        // 1.19's entities
        Mob ALLAY = new Mob(EntityType.ALLAY, Skulls.ALLAY.getSkull(plugin));
        Mob FROG = new Mob(EntityType.FROG, Skulls.TEMPERATE_FROG.getSkull(plugin));
        Mob TADPOLE = new Mob(EntityType.TADPOLE, Skulls.TADPOLE.getSkull(plugin));
        Mob WARDEN = new Mob(EntityType.WARDEN, Skulls.WARDEN.getSkull(plugin));
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
        private final ItemStack head;
        private final String permission;

        Mob(EntityType type, ItemStack head) {
            this.type = type;
            this.head = head;
            this.permission = "landlord.player.manage.mob-spawning." + type.name();

            MOBS.add(this);
        }

        @Override
        public EntityType getType() {
            return type;
        }

        @Override
        public ItemStack getHead() {
            return head.clone();
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
