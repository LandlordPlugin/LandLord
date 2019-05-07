package biz.princeps.landlord.api;

import org.bukkit.Material;

public interface ILLFlag {

    String getName();

    void toggle();

    Material getMaterial();

    String getStatus();
}
