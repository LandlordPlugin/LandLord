package biz.princeps.landlord.commands;

import biz.princeps.landlord.util.OwnedLand;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by spatium on 19.07.17.
 */
public class Manage extends LandlordCommand {


    public void onManage(Player player, @Nullable String[] args) {

        OwnedLand land = plugin.getWgHandler().getRegion(player.getLocation().getChunk());

        if (land == null) {
            player.sendMessage(lm.getString("Commands.Manage.notOwnFreeLand"));
            return;
        }

        if (!land.isOwner(player.getUniqueId())) {
            player.sendMessage(lm.getString("Commands.Manage.notOwn"));
            return;
        }
        if (args.length == 0) {
            String toggle = lm.getRawString("Commands.Manage.toggle");

            List<String> lines = lm.getStringList("Commands.Manage.list");

            ComponentBuilder builder = new ComponentBuilder("");
            lines.forEach(s -> {
                boolean flag = true;
                if (s.contains("%allow%")) {
                    builder.append(s.replaceAll("%allow%", toggle));
                    builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/land manage allow"));
                } else if (s.contains("%regen%")) {
                    builder.append(s.replaceAll("%regen%", toggle));
                    builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/land manage regen"));
                } else if (s.contains("%greet%")) {
                    builder.append(s.replaceAll("%greet%", land.getLand().getFlag(DefaultFlag.GREET_MESSAGE)));
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setgreet "));
                } else if (s.contains("%farewell%")) {
                    builder.append(s.replaceAll("%farewell%", land.getLand().getFlag(DefaultFlag.FAREWELL_MESSAGE)));
                    builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/land manage setfarewell "));
                } else if (s.contains("%friends%")) {
                    if (land.getLand().getMembers().getUniqueIds().size() == 0) flag = false;

                    builder.append(s.replaceAll("%friends%", ""));
                    Iterator<UUID> iterator = land.getLand().getMembers().getUniqueIds().iterator();

                    while (iterator.hasNext()) {
                        UUID id = iterator.next();
                        String name = Bukkit.getOfflinePlayer(id).getName();
                        builder.append(name);
                        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/land unfriend " + name));
                        System.out.println("clicked unfriednd");
                        if (iterator.hasNext())
                            builder.append(", ");
                    }
                } else if (s.contains("%land%"))
                    builder.append(s.replaceAll("%land%", land.getLandName()));
                else
                    flag = false;


                if (flag)
                    builder.append("\n");
            });

            player.spigot().sendMessage(builder.create());

            return;
        }

        switch (args[0]) {
            case "allow":
                StateFlag.State state = StateFlag.State.ALLOW;

                if (land.getLand().getFlag(DefaultFlag.BUILD) == StateFlag.State.ALLOW)
                    state = StateFlag.State.DENY;

                land.getLand().setFlag(DefaultFlag.BUILD, state);

                player.sendMessage(lm.getString("Commands.Manage.toggledAllow")
                        .replaceAll("%state%", state.name()));
                break;
            case "regen":
                player.sendMessage("regeb");
                break;
            case "setgreet":
                break;

            case "setfarewell":
                break;
        }
    }
}
