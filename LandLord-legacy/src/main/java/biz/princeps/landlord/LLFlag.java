package biz.princeps.landlord;

import biz.princeps.landlord.api.ILLFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;

/**
 * Note on {@link RegionGroup#MEMBERS} and {@link RegionGroup#NON_MEMBERS check}.
 * From WorldGuard wiki: The {@code entry} and {@code exit} flags default to {@code non-member}, meaning setting them to
 * {@code deny} will prevent non-members from entering/exiting the region.
 */
public class LLFlag implements ILLFlag {

    private final ProtectedRegion pr;

    private boolean friendStatus, allStatus;
    private final Flag<StateFlag.State> flag;
    private final Material mat;

    public LLFlag(ProtectedRegion pr, Flag<StateFlag.State> flag, Material mat) {
        this.pr = pr;
        this.flag = flag;
        this.mat = mat;

        RegionGroup regionGroupFlag = (RegionGroup) pr.getFlags().get(flag.getRegionGroupFlag());
        StateFlag.State value = (StateFlag.State) pr.getFlags().get(flag);
        setStatus(regionGroupFlag, value);
    }

    void setStatus(RegionGroup grp, Object state) {
        if (grp == RegionGroup.MEMBERS && state == StateFlag.State.ALLOW ||
                grp == RegionGroup.NON_MEMBERS && state == StateFlag.State.DENY) {
            friendStatus = true;
            allStatus = false;
            // System.out.println("10");
        }
        if (grp == RegionGroup.ALL && state == StateFlag.State.ALLOW) {
            friendStatus = true;
            allStatus = true;
            // System.out.println("11");
        }
        if (grp == RegionGroup.NON_OWNERS && state == StateFlag.State.DENY) {
            friendStatus = false;
            allStatus = false;
            // System.out.println("00");
        }
    }

    @Override
    public String getName() {
        return flag.getName();
    }

    @Override
    public boolean toggleFriends() {
        if (friendStatus) {
            this.friendStatus = false;
            // deny everyone
            if (allStatus) {
                return false;
            } else {
                pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
                pr.setFlag(flag, StateFlag.State.DENY);
                return true;
            }

        } else {
            // allow friends now
            if (allStatus) {
                // now: 11
                this.friendStatus = true;
                pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);
                pr.setFlag(flag, StateFlag.State.ALLOW);
            } else {
                // now 10
                this.friendStatus = true;
                if (flag.getRegionGroupFlag().getDefault() != RegionGroup.NON_MEMBERS) {
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    pr.setFlag(flag, StateFlag.State.ALLOW);
                } else {
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                    pr.setFlag(flag, StateFlag.State.DENY);
                }
            }
            return true;
        }
    }

    @Override
    public boolean toggleAll() {
        if (allStatus) {
            // deny nonmbers now
            this.allStatus = false;
            if (friendStatus) {
                // still allow friends
                if (flag.getRegionGroupFlag().getDefault() != RegionGroup.NON_MEMBERS) {
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
                    pr.setFlag(flag, StateFlag.State.ALLOW);
                } else {
                    pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
                    pr.setFlag(flag, StateFlag.State.DENY);
                }
            } else {
                // deny everyone
                pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.NON_OWNERS);
                pr.setFlag(flag, StateFlag.State.DENY);
            }
            return true;
        } else {
            if (friendStatus) {
                this.allStatus = true;
                pr.setFlag(flag.getRegionGroupFlag(), RegionGroup.ALL);
                pr.setFlag(flag, StateFlag.State.ALLOW);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Material getMaterial() {
        return mat;
    }

    @Override
    public boolean getFriendStatus() {
        return friendStatus;
    }

    @Override
    public boolean getAllStatus() {
        return allStatus;
    }
}
