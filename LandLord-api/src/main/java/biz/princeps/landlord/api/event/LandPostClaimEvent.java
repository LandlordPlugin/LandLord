package biz.princeps.landlord.api.event;

import biz.princeps.landlord.api.ClaimType;
import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 12/5/17
 * <p>
 * This event is called, as soon as the land claim process is finished and the player actually owns the land
 * @param player the player who claimed the land
 * @param land the bought land.
 * @param type the claim type of.
 */
public record LandPostClaimEvent(
    Player player,
    IOwnedLand land,
    ClaimType type
) implements LandLordEvent {

}
