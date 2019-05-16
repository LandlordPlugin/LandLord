package biz.princeps.landlord.api;

import java.util.UUID;

public interface ICostManager {

    double calculateCost(UUID id);

    double calculateCost(int x);
}
