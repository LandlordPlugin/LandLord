package biz.princeps.landlord.api;

import org.bukkit.entity.EntityType;

import java.util.Collection;

public interface IMobManager {

    IMob valueOf(String name);

    IMob get(EntityType type);

    Collection<IMob> values();
}
