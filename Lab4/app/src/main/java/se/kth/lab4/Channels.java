package se.kth.lab4;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by robscott on 2017-01-15.
 */

public class Channels implements Serializable {
    private String name;

    public Channels() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public HashMap<String,String> toFireBaseChannel(){
        HashMap<String,String> channelObject = new HashMap<String, String>();
        channelObject.put("name",name);
        return channelObject;
    }
}