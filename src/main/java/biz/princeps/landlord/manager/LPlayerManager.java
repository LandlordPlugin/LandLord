package biz.princeps.landlord.manager;

import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.manager.MappedManager;

import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
public class LPlayerManager extends MappedManager<UUID, LPlayer> {

    @Override
    public void saveAll() {

    }

    @Override
    public void loadAll() {

    }

    public void incrementLandCount(UUID id) {
        elements.get(id).setLandCount(elements.get(id).getLandCount() + 1);
    }

    public void decrementLandCount(UUID id) {
        elements.get(id).setLandCount(elements.get(id).getLandCount() - 1);
    }
}
