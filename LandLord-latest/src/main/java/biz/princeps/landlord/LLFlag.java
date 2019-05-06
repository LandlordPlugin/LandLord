package biz.princeps.landlord;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Material;

public class LLFlag {
    private OwnedLand land;
    private String status;
    private Flag flag;
    private Material mat;

    private StateFlag.State state1, state2;
    private String g1, g2;

    public LLFlag(Flag flag, OwnedLand land, Material mat) {
        this.land = land;
        this.flag = flag;
        this.mat = mat;

        if (land.getWGLand().getFlags().containsKey(flag))
            status = land.getWGLand().getFlags().get(flag).toString();
        else
            status = "NaN";
    }


    public void setToggle(StateFlag.State one, String group, StateFlag.State two, String grp2) {
        this.state1 = one;
        this.state2 = two;
        this.g1 = group;
        this.g2 = grp2;
    }

    public void toggle() {
        ProtectedRegion pr = land.getWGLand();

        if (flag instanceof StateFlag) {
            if (pr.getFlags().get(flag) == state1) {

                if (g2.equals("nonmembers"))
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                else
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);

                pr.setFlag(flag, state2);
                this.status = state2.name();

            } else {
                if (g1.equals("nonmembers"))
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                else
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);
                pr.setFlag(flag, state1);
                this.status = state1.name();

            }
        }
    }

    public Material getMaterial() {
        return mat;
    }

    public String getStatus() {
        return status;
    }

    public Flag<?> getWGFlag() {
        return flag;
    }
}
