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
    public int processOperations(int limit) {
        if (!player.isOnline()) {
            clear();
            return 0;
        }
        int iterations = 0;

        for (Iterator<Chunk> iterator = queue.iterator(); iterator.hasNext() && iterations < limit; ) {
            Chunk chunk = iterator.next();

            claim.onClaim(player, chunk);

            iterator.remove();
            iterations++;
        }

        return iterations;
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || !player.isOnline();
    }

}
