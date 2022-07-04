package biz.princeps.landlord.api.event;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;

/**
 * Called whenever a land ist being managed (on click in the gui). Doesn't trigger for setfarewell-like flags.
 */
public record LandManageEvent(
    Player player,
    IOwnedLand land,
    String flagChanged,
    Object oldValue,
    Object newValue
) implements LandLordEvent {

}
