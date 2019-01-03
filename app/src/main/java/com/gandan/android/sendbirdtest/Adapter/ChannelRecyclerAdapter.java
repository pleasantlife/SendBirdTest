package com.gandan.android.sendbirdtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;

import java.util.List;

public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelRecyclerHolder> {

    Context context;
    List<GroupChannel> groupChannelList;
    List<OpenChannel> openChannelList;
    List<BaseChannel> baseChannelList;
    int size = 0;

    public ChannelRecyclerAdapter(Context context, List<GroupChannel> groupChannelList, List<OpenChannel> openChannelList){
        this.context = context;
        this.groupChannelList = groupChannelList;
        this.openChannelList = openChannelList;
        this.size = groupChannelList.size() + openChannelList.size();
    }

    public ChannelRecyclerAdapter(Context context, List<BaseChannel> baseChannelList){
        this.context = context;
        this.baseChannelList = baseChannelList;
        this.size = baseChannelList.size();
    }


    @NonNull
    @Override
    public ChannelRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ChannelRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelRecyclerHolder holder, int position) {
        /*if(position >= groupChannelList.size()){
            final OpenChannel openChannel = openChannelList.get(position - groupChannelList.size());
            holder.text1.setText(openChannel.getName());
            holder.text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, openChannel.getCreatedAt()+"", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final GroupChannel groupChannel = groupChannelList.get(position);
            holder.text1.setText(groupChannel.getName());
            holder.text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, groupChannel.getCreatedAt()+"", Toast.LENGTH_SHORT).show();
                }
            });
        }*/
        final BaseChannel baseChannel = baseChannelList.get(position);
        holder.text1.setText(baseChannel.getName());
        holder.text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, baseChannel.getCreatedAt()+"", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return size;
    }

    class ChannelRecyclerHolder extends RecyclerView.ViewHolder {

        TextView text1;

        public ChannelRecyclerHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
