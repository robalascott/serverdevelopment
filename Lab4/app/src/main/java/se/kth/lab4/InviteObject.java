package se.kth.lab4;

/**
 * Created by robscott on 2017-01-22.
 */
public class InviteObject {
    private String groupname;
    private String ownerName;

    public InviteObject(String gg, String oN){
        this.groupname=gg;
        this.ownerName=oN;
    }
    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
