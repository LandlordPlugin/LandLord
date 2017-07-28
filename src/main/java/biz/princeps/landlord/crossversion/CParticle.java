package biz.princeps.landlord.crossversion;

/**
 * Created by spatium on 28.07.17.
 */
public enum CParticle {

    VILLAGERHAPPY("HAPPY_VILLAGER", "VILLAGER_HAPPY"),
    DRIPWATER("WATERDRIP", "DRIP_WATER"),
    DRIPLAVA("LAVADRIP", "DRIP_LAVA");

    private final String v18;
    private final String v19;

    CParticle(String v18, String v19) {
        this.v18 = v18;
        this.v19 = v19;
    }

    public String getV18() {
        return v18;
    }

    public String getV19() {
        return v19;
    }
}
