package biz.princeps.landlord;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.IWorldGuardManager;
import biz.princeps.landlord.api.events.LandChangeEvent;
import biz.princeps.landlord.manager.WorldGuardManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FarewellFlag;
import com.sk89q.worldguard.session.handler.GreetingFlag;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class LandSessionHandler extends Handler {

    private final ILandLord plugin;
    private final IWorldGuardManager worldGuardManager;
    private final GreetingFlag greeting;
    private final FarewellFlag farewell;

    private LandSessionHandler(ILandLord plugin, Session session, IWorldGuardManager worldGuardManager) {
        super(session);
        this.plugin = plugin;
        this.worldGuardManager = worldGuardManager;
        this.greeting = GreetingFlag.FACTORY.create(session);
        this.farewell = FarewellFlag.FACTORY.create(session);
    }

    @Override
    public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet,
                                   Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (entered.isEmpty() && exited.isEmpty()) {
            // for whatever reason, ON CROSS BOUNDARY is triggered when not crossing boundaries
            // even worse, the toSet isn't empty and will return greeting/farewell messages to WG
            // yes, it even finds the farewell message... ???
            // from my testings, it still works as expected by just using entered/exited
            return true;
        }
        IOwnedLand enteredLand = getFirstOwnedLand(entered);
        IOwnedLand exitedLand = getFirstOwnedLand(exited);
        if (enteredLand == null && exitedLand == null) {
            // neither entered nor exited a LL land, let WG handle it
            return this.greeting.onCrossBoundary(player, from, to, toSet, entered, exited, moveType)
                    && this.farewell.onCrossBoundary(player, from, to, toSet, entered, exited, moveType);
        }
        if (enteredLand != null && exitedLand != null) {
            if (enteredLand.getOwner().equals(exitedLand.getOwner())) {
                return true; // switched between two lands of the same owner
            }
        }
        if (enteredLand == null && !entered.isEmpty()) {
            // entered a non-ll region, needs to be handled by WG
            this.greeting.onCrossBoundary(player, from, to, toSet, entered, exited, moveType);
        }
        if (exitedLand == null && !exited.isEmpty()) {
            // exited a non-ll region, needs to be handled by WG
            this.farewell.onCrossBoundary(player, from, to, toSet, entered, exited, moveType);
        }
        plugin.getPlugin().getServer().getPluginManager().callEvent(new LandChangeEvent(player, exitedLand, enteredLand));
        return true; // we're not handling anything here for now
    }

    // assuming lands don't overlap, as it should be
    private IOwnedLand getFirstOwnedLand(Set<ProtectedRegion> regions) {
        for (ProtectedRegion region : regions) {
            IOwnedLand land = this.worldGuardManager.getRegion(region.getId());
            if (land != null) {
                return land;
            }
        }
        return null;
    }

    public static final class Factory extends Handler.Factory<LandSessionHandler> {

        private final ILandLord plugin;
        private final WorldGuardManager worldGuardManager;

        public Factory(ILandLord plugin, WorldGuardManager worldGuardManager) {
            this.plugin = plugin;
            this.worldGuardManager = worldGuardManager;
        }

        @Override
        public LandSessionHandler create(Session session) {
            return new LandSessionHandler(plugin, session, this.worldGuardManager);
        }
    }
}
