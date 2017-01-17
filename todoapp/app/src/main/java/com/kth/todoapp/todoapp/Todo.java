package com.kth.todoapp.todoapp;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by robscott on 2017-01-12.
 */

public class Todo implements Serializable {
    private String owner;
    private String message;
    private String date;
    private String key;



    public Todo(){}
    public Todo(String owner,String message,String date){
        this.setDate (date);
        this.setMessage (message);
        this.setOwner (owner);
    }
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String,String> toFireBaseTodo(){
        HashMap<String,String> todo = new HashMap<String, String>();
        todo.put("owner",owner);
        todo.put("message",message);
        todo.put("date", date);
        return todo;
    }
    public boolean checkValues(){
        if(owner.isEmpty() || owner == null){
            return false;
        }else if(message.isEmpty() ||message == null){
            return false;
        }
        return true;
    }
    public void clear(){
        this.message = null;
        this.owner = null;
        this.date = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
