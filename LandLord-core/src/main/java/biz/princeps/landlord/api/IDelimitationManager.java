package biz.princeps.landlord.api;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public interface IDelimitationManager {

    void delimit(Player player, Chunk chunk);
}
