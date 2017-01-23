package se.kth.lab4;

import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robscott on 2017-01-22.
 */

public class InvitationsAdapter extends RecyclerView.Adapter< InvitationsAdapter.InviteViewHolder > {
    private InviteClickListener listner;
    private List<Invitation> list;

    public InvitationsAdapter(List<Invitation> temp ){
        this.list = temp;
    }
    public interface InviteClickListener{
        public void onItemClick(View v,int pos );
    }
    public void setOnClick(InviteClickListener listner){
        this.listner = listner;
    }
    @Override
    public InvitationsAdapter.InviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitesitems, parent, false);
        return new InviteViewHolder (v);
    }

    @Override
    public void onBindViewHolder(InvitationsAdapter.InviteViewHolder  holder, int position) {
        Invitation object = list.get(position);
        holder.inviteNameView.setText(object.getGroupId());
        holder.inviteGroupView.setText(object.getGroupName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class InviteViewHolder extends RecyclerView.ViewHolder{
        private TextView  inviteGroupView,inviteNameView;
        public InviteViewHolder (View v){
            super(v);
            inviteGroupView = (TextView) itemView.findViewById(R.id.groupInviteTextView);
            inviteNameView = (TextView) itemView.findViewById(R.id.ownerInviteTextView);
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
