package se.kth.lab4;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.nio.channels.Channel;
import java.util.List;

/**
 * Created by robscott on 2017-01-15.
 */

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.MyChannelViewHolder> {
    private List<Channels> channelsList;
    private Context con;
    private ChannelClickListener listner;

    public ChannelsAdapter(List<Channels> list,Context context){
        this.channelsList = list;
        this.con = context;
    }
    public interface ChannelClickListener{
        public void onChannelClick(View v,int pos );
    }
    public void setOnChannelClick(ChannelClickListener listner){
        this.listner = listner;
    }


    @Override
    public ChannelsAdapter.MyChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channelobject, parent, false);
        return new MyChannelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChannelsAdapter.MyChannelViewHolder holder, int position) {
        Channels channels = channelsList.get(position);
        holder.channelView.setText( channels.getName ());
    }

    @Override
    public int getItemCount() {
        return channelsList.size();
    }

    public class MyChannelViewHolder extends RecyclerView.ViewHolder{
        private TextView channelView;
        public MyChannelViewHolder(View v){
            super(v);
            channelView = (TextView) itemView.findViewById(R.id.channelTextView);

            v.setOnClickListener (new View.OnClickListener (){
                @Override
                public void onClick(View v) {
                    if(listner!=null){
                        int position = getAdapterPosition ();
                        if(position!=RecyclerView.NO_POSITION){
                            listner.onChannelClick (v,position);
                        }
                    }
                }
            });
        }

    }
}