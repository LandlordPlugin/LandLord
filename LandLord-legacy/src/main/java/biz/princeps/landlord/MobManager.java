package biz.princeps.landlord;

import biz.princeps.landlord.api.IMob;
import biz.princeps.landlord.api.IMobManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class MobManager implements IMobManager {


    static Collection<IMob> MOBS = new ArrayList<>();
    public static final Mob ELDER_GUARDIAN = new Mob(EntityType.ELDER_GUARDIAN, 4);
    public static final Mob WITHER_SKELETON = new Mob(EntityType.WITHER_SKELETON, 5);
    public static final Mob STRAY = new Mob(EntityType.STRAY, 6);
    public static final Mob HUSK = new Mob(EntityType.HUSK, 23);
    public static final Mob ZOMBIE_VILLAGER = new Mob(EntityType.ZOMBIE_VILLAGER, 27);
    public static final Mob SKELETON_HORSE = new Mob(EntityType.SKELETON_HORSE, 28);
    public static final Mob ZOMBIE_HORSE = new Mob(EntityType.ZOMBIE_HORSE, 29);
    public static final Mob DONKEY = new Mob(EntityType.DONKEY, 31);
    public static final Mob MULE = new Mob(EntityType.MULE, 32);
    public static final Mob EVOKER = new Mob(EntityType.EVOKER, 34);
    public static final Mob VEX = new Mob(EntityType.VEX, 35);
    public static final Mob VINDICATOR = new Mob(EntityType.VINDICATOR, 36);
    public static final Mob CREEPER = new Mob(EntityType.CREEPER, 50);
    public static final Mob SKELETON = new Mob(EntityType.SKELETON, 51);
    public static final Mob SPIDER = new Mob(EntityType.SPIDER, 52);
    public static final Mob ZOMBIE = new Mob(EntityType.ZOMBIE, 54);
    public static final Mob SLIME = new Mob(EntityType.SLIME, 55);
    public static final Mob GHAST = new Mob(EntityType.GHAST, 56);
    public static final Mob PIG_ZOMBIE = new Mob(EntityType.PIG_ZOMBIE, 57);
    public static final Mob ENDERMAN = new Mob(EntityType.ENDERMAN, 58);
    public static final Mob CAVE_SPIDER = new Mob(EntityType.CAVE_SPIDER, 59);
    public static final Mob SILVERFISH = new Mob(EntityType.SILVERFISH, 60);
    public static final Mob BLAZE = new Mob(EntityType.BLAZE, 61);
    public static final Mob MAGMA_CUBE = new Mob(EntityType.MAGMA_CUBE, 62);
    public static final Mob BAT = new Mob(EntityType.BAT, 65);
    public static final Mob WITCH = new Mob(EntityType.WITCH, 66);
    public static final Mob GUARDIAN = new Mob(EntityType.GUARDIAN, 68);
    public static final Mob PIG = new Mob(EntityType.PIG, 90);
    public static final Mob SHEEP = new Mob(EntityType.SHEEP, 91);
    public static final Mob COW = new Mob(EntityType.COW, 92);
    public static final Mob CHICKEN = new Mob(EntityType.CHICKEN, 93);
    public static final Mob SQUID = new Mob(EntityType.SQUID, 94);
    public static final Mob WOLF = new Mob(EntityType.WOLF, 95);
    public static final Mob OCELOT = new Mob(EntityType.OCELOT, 98);
    public static final Mob HORSE = new Mob(EntityType.HORSE, 100);
    public static final Mob RABBIT = new Mob(EntityType.RABBIT, 101);
    public static final Mob POLAR_BEAR = new Mob(EntityType.POLAR_BEAR, 102);
    public static final Mob LLAMA = new Mob(EntityType.LLAMA, 103);
    public static final Mob PARROT = new Mob(EntityType.PARROT, 105);
    public static final Mob VILLAGER = new Mob(EntityType.VILLAGER, 120);

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
        int egg;


        Mob(EntityType t, int egg) {
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
            return new ItemStack(Material.MONSTER_EGG, 1, (short) egg);
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
        public String getName(){
            return t.getName().toUpperCase();
        }
    }
}