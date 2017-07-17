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


    @Override
    public void addAll(Map<UUID, LPlayer> map) {

    }
}
