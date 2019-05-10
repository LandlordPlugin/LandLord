package biz.princeps.landlord.api;

import biz.princeps.landlord.manager.CostManager;

import java.util.UUID;

public interface ICostManager {

    CostManager.COST_FUNCTION getSelectedFunction();

    double calculateCost(UUID id);

    double calculateCost(int x);
}
