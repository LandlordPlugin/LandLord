package biz.princeps.landlord.api;

import java.util.UUID;

public interface IClaimableLand extends ILand {

    IPossessedLand claim(UUID id);
}
