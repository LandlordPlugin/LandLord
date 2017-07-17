package biz.princeps.landlord.manager;

import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.manager.MappedManager;
import biz.princeps.lib.storage.DatabaseAPI;

import java.util.Map;
import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
public class LPlayerManager extends MappedManager<UUID, LPlayer> {

    public LPlayerManager(DatabaseAPI api) {
        super(api);
    }

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

    @Override
    public void addAll(Map<UUID, LPlayer> map) {

    }
}
