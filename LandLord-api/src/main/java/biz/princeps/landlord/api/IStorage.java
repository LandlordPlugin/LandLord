package biz.princeps.landlord.api;

import java.util.UUID;
import java.util.function.Consumer;

public interface IStorage {

    void getPlayer(UUID id, Consumer<IPlayer> consumer);

    IPlayer getPlayer(UUID id);

    void savePlayer(IPlayer p, boolean async);
}
