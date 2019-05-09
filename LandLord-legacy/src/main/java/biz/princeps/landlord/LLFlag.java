package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;

public class LLFlag implements ILLFlag {

    private ProtectedRegion pr;

    private String curr_status;
    private Flag flag;
    private Material mat;

    private StateFlag.State state1, state2;
    private boolean isGroup1, isGroup2;

    public LLFlag(ProtectedRegion pr, Flag flag, Material mat, StateFlag.State state1, StateFlag.State state2, boolean g1, boolean g2) {
        this.pr = pr;
        this.flag = flag;
        this.mat = mat;
        this.state1 = state1;
        this.state2 = state2;
        this.isGroup1 = g1;
        this.isGroup2 = g2;

        this.curr_status = pr.getFlags().get(flag).toString();
    }

    @Override
    public String getName() {
        return flag.getName();
    }

    @Override
    public void toggle() {
        if (curr_status.equalsIgnoreCase(state1.name())) {
            handleToggle(isGroup1, state2);
        } else {
            handleToggle(isGroup2, state1);
        }
    }

    @Override
    public Material getMaterial() {
        return mat;
    }

    @Override
    public String getStatus() {
        return curr_status;
    }

    private void handleToggle(boolean g, StateFlag.State state) {
        if (g)
            pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
        else
            pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);
        pr.setFlag(flag, state);
        this.curr_status = state.name();
    }

    @Override
    public String toString() {
        return "LLFlag{" +
                "pr=" + pr +
                ", curr_status='" + curr_status + '\'' +
                ", flag=" + flag +
                ", mat=" + mat +
                ", state1=" + state1 +
                ", state2=" + state2 +
                ", isGroup1=" + isGroup1 +
                ", isGroup2=" + isGroup2 +
                '}';
    }
}
