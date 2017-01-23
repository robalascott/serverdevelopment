package se.kth.lab4;

/**
 * Created by Daniel on 2017-01-22.
 */
public class Invitation {

    private String groupName;
    private String groupId;
    private String sender;

    public Invitation(String groupName, String groupId, String sender){
        this.groupName = groupName;
        this.groupId = groupId;
        this.sender = sender;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
