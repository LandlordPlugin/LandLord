package biz.princeps.landlord.manager;

import biz.princeps.landlord.Landlord;
import biz.princeps.landlord.persistent.LPlayer;
import biz.princeps.lib.manager.MappedManager;
import biz.princeps.lib.storage.DatabaseAPI;

import java.util.UUID;

/**
 * Created by spatium on 17.07.17.
 */
public class LPlayerManager extends MappedManager<UUID, LPlayer> {

    public LPlayerManager(DatabaseAPI api) {
        super(api);
    }

    public void save(UUID id) {
        LPlayer lp = get(id);
        Landlord.getInstance().getDatabaseAPI().saveObject(lp);
    }
}
