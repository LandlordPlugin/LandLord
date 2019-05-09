package biz.princeps.landlord;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum MobsProxy {

    ;

    EntityType t;
    Material egg;

    MobsProxy(EntityType t, Material egg) {
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
}
