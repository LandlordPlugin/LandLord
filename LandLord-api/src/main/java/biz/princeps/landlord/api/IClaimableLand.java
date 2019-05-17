package biz.princeps.landlord.api;

import java.util.UUID;

public interface IClaimableLand extends ILand {

    void claim(UUID id);
}
