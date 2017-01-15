package com.kth.todoapp.todoapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by robscott on 2017-01-14.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {
    private List<Todo> todoList;
    private Context con;
    private ItemClickListener listner;
    public interface ItemClickListener{
        public void onItemClick(View v,int pos );
    }
    public void setOnClick(ItemClickListener listner){
        this.listner = listner;
    }


    public TodoAdapter(List<Todo> list,Context context){
        this.todoList = list;
        this.con = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todoitems, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.messageView.setText(todo.getMessage());
        holder.ownerView.setText(todo.getOwner());
        holder.dateView.setText(todo.getDate());
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView messageView,ownerView,dateView;
        public MyViewHolder(View v){
            super(v);
            messageView = (TextView) itemView.findViewById(R.id.messageTextView);
            ownerView = (TextView) itemView.findViewById(R.id.ownerTextView);
            dateView = (TextView) itemView.findViewById(R.id.dateTextView);
            v.setOnClickListener (new View.OnClickListener (){
                @Override
                public void onClick(View v) {
                    if(listner!=null){
                        int position = getAdapterPosition ();
                        if(position!=RecyclerView.NO_POSITION){
                            listner.onItemClick (v,position);
                        }
                    }
                }
            });
        }

    }
 }
