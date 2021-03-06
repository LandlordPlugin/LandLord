package biz.princeps.landlord.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IVaultManager {

    double getBalance(Player player);

    boolean hasBalance(Player player, double amt);

    void take(Player player, double amt);

    void give(UUID uuid, double amt, World world);

    void give(Player player, double amt);

    String format(double amt);
}

