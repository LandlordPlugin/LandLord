package biz.princeps.landlord.commands.admin;

import biz.princeps.lib.command.Arguments;
import biz.princeps.lib.command.Properties;
import biz.princeps.lib.command.SubCommand;
import com.google.common.collect.Sets;
import de.eldoria.eldoutilities.debug.DebugSettings;
import de.eldoria.eldoutilities.debug.DebugUtil;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;

public class Debug extends SubCommand {
    private final Plugin plugin;

    public Debug(Plugin plugin) {
        super("debug",
                "/ll debug",
                Sets.newHashSet(Arrays.asList("landlord.admin", "eldoutilities.debug")),
                Collections.emptySet());
        this.plugin = plugin;
    }

    @Override
    public void onCommand(Properties properties, Arguments arguments) {
        DebugUtil.dispatchDebug(properties.getCommandSender(), plugin, DebugSettings.DEFAULT);
    }
}
