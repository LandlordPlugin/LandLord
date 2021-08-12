package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.AMultiTask;
import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.commands.claiming.Claim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;

public class MultiClaimTask extends AMultiTask<Chunk> {

    private final Claim claim;
    private final Player player;

    public MultiClaimTask(ILandLord plugin, Player player, Collection<Chunk> operations, Claim claim) {
        super(plugin, operations);

        this.claim = claim;
        this.player = player;
    }

    @Override
    public boolean process(Chunk chunk) {
        claim.onClaim(player, chunk);

        return true;
    }

    @Override
    public boolean canProcess() {
        return player.isOnline();
    }

}
