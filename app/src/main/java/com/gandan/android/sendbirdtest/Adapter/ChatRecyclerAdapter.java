package com.gandan.android.sendbirdtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;

import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder> {

    Context context;
    List<BaseMessage> baseMessageList;

    public ChatRecyclerAdapter(Context context, List<BaseMessage> baseMessageList){
        this.context = context;
        this.baseMessageList = baseMessageList;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        BaseMessage baseMessage = baseMessageList.get(position);
        holder.text1.setText(baseMessage.getData()+"");
    }

    @Override
    public int getItemCount() {
        return baseMessageList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        TextView text1;

        public ChatHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
