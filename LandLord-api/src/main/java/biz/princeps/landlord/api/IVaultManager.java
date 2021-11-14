package biz.princeps.landlord.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IVaultManager {

    double getBalance(Player player);

    boolean hasBalance(Player player, double amount);

    void take(Player player, double amount);

    void give(UUID uuid, double amount, World world);

    void give(Player player, double amount);

    String format(double amount);
}

