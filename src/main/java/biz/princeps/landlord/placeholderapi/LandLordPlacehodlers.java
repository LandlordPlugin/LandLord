package biz.princeps.landlord.placeholderapi;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.util.OwnedLand;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LandLordPlacehodlers extends EZPlaceholderHook {

    private Landlord pl;

    public LandLordPlacehodlers(Landlord plugin) {
        super(plugin, "ll");
        this.pl = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {
        switch (s) {

            case "ownedlands":
                int landcount = 0;
                for (World world : Bukkit.getWorlds()) {
                    landcount += pl.getWgHandler().getWG().getRegionManager(world).getRegionCountOfPlayer(pl.getWgHandler().getWG().wrapPlayer(player));
                }
                return String.valueOf(landcount);

            case "claims":
                return String.valueOf(pl.getPlayerManager().get(player.getUniqueId()).getClaims());

            case "currentLandOwner":
                OwnedLand land = pl.getWgHandler().getRegion(player.getLocation());
                if(land != null)
                    return land.printOwners();

            case "currentLandName":
                return OwnedLand.getName(player.getLocation().getChunk());
        }
        return null;
    }
}
