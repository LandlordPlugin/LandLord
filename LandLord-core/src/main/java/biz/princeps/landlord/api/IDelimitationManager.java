package biz.princeps.landlord.api;

import biz.princeps.landlord.util.DelimitationManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public interface IDelimitationManager {
    Map<DelimitationManager.BlockVector, Material> getPattern();

    void delimit(Player player, Chunk chunk);
}
