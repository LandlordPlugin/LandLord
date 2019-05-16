package biz.princeps.landlord.api;

import java.util.UUID;

public interface IOffer {
    String getLandname();

    double getPrice();

    UUID getSeller();
}
