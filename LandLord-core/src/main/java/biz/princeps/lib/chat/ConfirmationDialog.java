package biz.princeps.lib.chat;

import biz.princeps.lib.PrincepsLib;
import biz.princeps.lib.gui.simple.Action;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: LandLord
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 8/04/18
 */
public class ConfirmationDialog {

    static {
        new DialogHandler();
    }

    private final String message;
    private final String confirmCommand;

    private final Action onAccept;
    private final Action onDeny;

    // in seconds
    private final int acceptTimout;
    private BukkitRunnable runnable;

    public ConfirmationDialog(String message, String confirmCommand, Action onAccept, Action onDeny, int acceptTimout) {
        this.message = message;
        this.confirmCommand = confirmCommand;
        this.onAccept = onAccept;
        this.onDeny = onDeny;
        this.acceptTimout = acceptTimout;
    }

    public void display(Player p) {
        p.spigot().sendMessage(new ComponentBuilder("").append(TextComponent.fromLegacyText(message))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, confirmCommand)).create());
        DialogHandler.addPlayer(p.getUniqueId(), this);
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (DialogHandler.contains(p.getUniqueId())) {
                    DialogHandler.removePlayer(p.getUniqueId());
                    if (p.isOnline()) {
                        onDeny.execute(p);
                    }
                }
            }
        };
        runnable.runTaskLater(PrincepsLib.getPluginInstance(), acceptTimout * 20);
    }


    static class DialogHandler implements Listener {

        private DialogHandler() {
            PrincepsLib.getPluginInstance().getServer().getPluginManager().registerEvents(this, PrincepsLib.getPluginInstance());
        }

        private static final Map<UUID, ConfirmationDialog> openDialogs = new HashMap<>();

        @EventHandler
        public void onCommand(PlayerCommandPreprocessEvent event) {
            if (contains(event.getPlayer().getUniqueId())) {
                ConfirmationDialog dialog = get(event.getPlayer().getUniqueId());
                if (dialog.confirmCommand.equals(event.getMessage())) {
                    dialog.onAccept.execute(event.getPlayer());
                    removePlayer(event.getPlayer().getUniqueId());
                    dialog.runnable.cancel();
                }
            }
        }

        public synchronized ConfirmationDialog get(UUID id) {
            return openDialogs.get(id);
        }

        public synchronized static void addPlayer(UUID uniqueId, ConfirmationDialog confirmationDialog) {
            openDialogs.put(uniqueId, confirmationDialog);
        }

        public synchronized static void removePlayer(UUID uniqueId) {
            openDialogs.remove(uniqueId);
        }

        public synchronized static boolean contains(UUID uuid) {
            return openDialogs.containsKey(uuid);
        }
    }
}
