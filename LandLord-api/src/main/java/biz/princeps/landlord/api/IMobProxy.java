package biz.princeps.landlord.api;

import org.bukkit.entity.EntityType;

import java.util.Collection;

public interface IMobProxy {

    IMob valueOf(String name);

    IMob get(EntityType type);

    Collection<IMob> values();
}
