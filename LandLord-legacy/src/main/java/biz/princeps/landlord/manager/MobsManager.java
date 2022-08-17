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

    private final ILandLord plugin;

    public MobsManager(ILandLord plugin) {
        this.plugin = plugin;

        Mob ELDER_GUARDIAN = new Mob(EntityType.ELDER_GUARDIAN, Skulls.ELDER_GUARDIAN.getSkull(plugin));
        Mob WITHER_SKELETON = new Mob(EntityType.WITHER_SKELETON, new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
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
        Mob CREEPER = new Mob(EntityType.CREEPER, new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
        Mob SKELETON = new Mob(EntityType.SKELETON, new ItemStack(Material.SKULL_ITEM, 1, (short) 0));
        Mob SPIDER = new Mob(EntityType.SPIDER, Skulls.SPIDER.getSkull(plugin));
        Mob ZOMBIE = new Mob(EntityType.ZOMBIE, new ItemStack(Material.SKULL_ITEM, 1, (short) 2));
        Mob SLIME = new Mob(EntityType.SLIME, Skulls.SLIME.getSkull(plugin));
        Mob GHAST = new Mob(EntityType.GHAST, Skulls.GHAST.getSkull(plugin));
        Mob PIG_ZOMBIE = new Mob(EntityType.PIG_ZOMBIE, Skulls.ZOMBIFIED_PIGLIN.getSkull(plugin));
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

        MOBS.sort(Comparator.comparing(iMob -> iMob.getType().name()));
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

        final EntityType type;
        final ItemStack head;
        final String permission;

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