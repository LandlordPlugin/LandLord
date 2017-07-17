package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.OwnedLand;
import biz.princeps.landlord.util.UUIDFetcher;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by spatium on 17.07.17.
 */
public class Addfriend extends LandlordCommand {

    public void onAddfriend(Player player, String[] names) {

        if (this.worldDisabled(player)) {
            player.sendMessage(lm.getString("Disabled-World"));
            return;
        }
        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        OwnedLand land = plugin.getWgHandler().getRegion(chunk);
        if (!land.isOwner(player.getUniqueId())) {
            player.sendMessage(lm.getString("Commands.Addfriend.notOwn")
                    .replaceAll("%owner%", land.printOwners()));
            return;
        }


        UUIDFetcher.getInstance().namesToUUID(names, new FutureCallback<DefaultDomain>() {
            @Override
            public void onSuccess(@Nullable DefaultDomain defaultDomain) {
                land.addFriends(defaultDomain);
                player.sendMessage(lm.getString("Commands.Addfriend.success")
                        .replaceAll("%players%", Arrays.asList(names).toString()));
            }

            @Override
            public void onFailure(Throwable throwable) {
                player.sendMessage(lm.getString("Commands.Addfriend.noPlayer")
                        .replaceAll("%players%", Arrays.asList(names).toString()));
            }
        });


    }

}

