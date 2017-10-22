package biz.princeps.landlord.flags;

import org.bukkit.Material;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 10/22/17
 */
public interface IFlag {
    void toggle();

    Material getMaterial();

    String getStatus();

    void setDefaultStatus();
}
