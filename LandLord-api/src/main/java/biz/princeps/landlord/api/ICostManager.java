package biz.princeps.landlord.api;

import java.util.UUID;

public interface ICostManager {

    /**
     * Calculate the cost to claim the next land for a player based on the configuration.
     * Internally the amount of owned lands is looked up in order to calculate this.
     *
     * @param id the uuid of the player
     * @return the cost for the next claim
     */
    double calculateCost(UUID id);

    /**
     * Calculates the cost to claim the next land based on the configuration.
     *
     * @param amtOfLands amount of current owned lands
     * @return the cost for the next claim
     */
    double calculateCost(int amtOfLands);
}
