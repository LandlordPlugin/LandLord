package biz.princeps.landlord.commands.management;

import biz.princeps.landlord.commands.LandlordCommand;
import biz.princeps.landlord.util.CommandUtil;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

/**
 * Created by spatium on 17.07.17.
 */
public class Info extends LandlordCommand {

    private String owned, free;

    public Info() {
        List<String> ownedList = plugin.getLangManager().getStringList("Commands.Info.owned");
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = ownedList.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append("\n");
        }
        owned = sb.toString();


        List<String> freeList = plugin.getLangManager().getStringList("Commands.Info.free");
        StringBuilder sb2 = new StringBuilder();
        Iterator<String> it2 = freeList.iterator();
        while (it2.hasNext()) {
            sb2.append(it2.next());
            if (it2.hasNext())
                sb2.append("\n");
        }
        free = sb2.toString();
    }

    public void onInfo( Player player) {
        CommandUtil.onInfo(player.getLocation(), player, lm);
    }
}
