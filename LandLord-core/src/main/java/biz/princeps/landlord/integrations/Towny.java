package biz.princeps.landlord.integrations;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import biz.princeps.landlord.listener.BasicListener;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;


public class Towny extends BasicListener {

    private final TownyAPI towny;
    private final ILandLord plugin;

    public Towny(ILandLord plugin) {
        super(plugin);
        this.towny = TownyAPI.getInstance();
        this.plugin = plugin;
    }

    @EventHandler
    public void onLLClaim(LandPreClaimEvent e) {
        Chunk chunk = e.getChunk();
        if (towny.isTownyWorld(e.getChunk().getWorld()) &&
                !towny.isWilderness(new Location(chunk.getWorld(), (chunk.getX() << 4) + 2, 2, (chunk.getZ() << 4) + 2))) {
            plugin.getLangManager().sendMessage(e.getPlayer(), "Integrations.Towny.TownyPresent");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTownyClaim(TownPreClaimEvent e) {
        TownBlock townBlock = e.getTownBlock();
        int x = townBlock.getX();
        int z = townBlock.getZ();

        Location locationAt = new Location(townBlock.getWorldCoord().getBukkitWorld(), x, 0, z);

        if (plugin.getWGManager().getRegion(locationAt) != null) {
            plugin.getLangManager().sendMessage(e.getPlayer(), "Integrations.Towny.LLPresent");
            e.setCancelled(true);
        }
    }

}
