package biz.princeps.landlord.api;

import org.bukkit.entity.Player;

import java.util.List;

public interface ILangManager {
    void reload();

    String getString(String path);

    String getTag();

    List<String> getStringList(String path);

    String getRawString(String path);

    void sendMessage(Player player, String msg);
}
