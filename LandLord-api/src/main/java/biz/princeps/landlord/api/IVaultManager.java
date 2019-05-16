package biz.princeps.landlord.api;

import java.util.UUID;

public interface IVaultManager {

    double getBalance(UUID id);

    boolean hasBalance(UUID id, double amt);

    void take(UUID id, double amt);

    void give(UUID id, double amt);

    String format(double amt);
}

