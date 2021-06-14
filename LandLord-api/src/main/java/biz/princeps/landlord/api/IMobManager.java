package biz.princeps.landlord.api;

import org.bukkit.entity.EntityType;

import java.util.Collection;

public interface IMobManager {

    /**
     * Gets the IMob for a name.
     * Upper/lowercase doesnt matter.
     *
     * @param name the name.
     * @return the IMob
     */
    IMob valueOf(String name);

    /**
     * Gets the IMob for a Bukkit EntityType
     *
     * @param type the EntityType
     * @return the IMob
     */
    IMob get(EntityType type);

    /**
     * Gets a list of all available Mobs.
     *
     * @return a list of all mobs
     */
    Collection<IMob> values();

}
