package biz.princeps.landlord.api.event;

import biz.princeps.landlord.api.IOwnedLand;
import org.bukkit.entity.Player;

/**
 * This event is fired when a player changes the land.
 *
 * @param player       the player who changes land.
 * @param previousLand the land the player was in previously or {@code null}
 *                     if and only if the player wasn't in a land before.
 * @param newLand      the land the player entered or {@code null} if and only
 *                     if the player wasn't in the land before.
 */
public record LandChangeEvent(
    Player player,
    IOwnedLand previousLand,
    IOwnedLand newLand
) implements LandLordEvent {

}
