package com.gandan.android.sendbirdtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gandan.android.sendbirdtest.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.BaseMessageParams;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder> {

    Context context;
    List<BaseMessage> baseMessageList;
    String userid;

    public ChatRecyclerAdapter(Context context, List<BaseMessage> baseMessageList, String userId){
        this.context = context;
        this.baseMessageList = baseMessageList;
        this.userid = userId;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        UserMessage message = (UserMessage) baseMessageList.get(position);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        holder.chatSenderTxtView.setText(message.getSender().getUserId()+"");
        holder.chatTxtView.setText(message.getMessage()+"");
        holder.chatTimeTxtView.setText(sdf.format(message.getCreatedAt()));
        holder.chatDateTxtView.setText(dateFormat.format(message.getCreatedAt()));
        if(position < baseMessageList.size()-1) {
            UserMessage beforeMessage = (UserMessage) baseMessageList.get(position+1);
            if(sdf.format(beforeMessage.getCreatedAt()).equals(sdf.format(message.getCreatedAt()))){
                holder.chatTimeTxtView.setVisibility(View.GONE);
            } else {
                holder.chatTimeTxtView.setVisibility(View.VISIBLE);
            }
            if(!dateFormat.format(beforeMessage.getCreatedAt()).equals(dateFormat.format(message.getCreatedAt()))){
                holder.chatDateTxtView.setVisibility(View.VISIBLE);
            } else {
                holder.chatDateTxtView.setVisibility(View.GONE);
            }


            if(!message.getSender().getUserId().equals(userid)){
                notMyMessageView(holder, lp);
            } else {
                myMessageView(holder, lp);
            }
        } else {
            /*UserMessage nextMessage = (UserMessage) baseMessageList.get(position+1);
            if(!dateFormat.format(nextMessage.getCreatedAt()).equals(dateFormat.format(message.getCreatedAt()))){
                holder.chatDateTxtView.setVisibility(View.VISIBLE);
            } else {
                holder.chatDateTxtView.setVisibility(View.GONE);
            }
            if(!nextMessage.getSender().getUserId().equals(message.getSender().getUserId())){
                notMyMessageView(holder, lp);
            } else {
                myMessageView(holder, lp);
            }*/
        }
    }

    private void myMessageView(ChatHolder holder, LinearLayout.LayoutParams layoutParams) {
        holder.chatSenderTxtView.setVisibility(View.GONE);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.topMargin = 24;
        holder.chatSenderTxtView.setLayoutParams(layoutParams);
        holder.chatTxtView.setLayoutParams(layoutParams);
        holder.chatTimeTxtView.setLayoutParams(layoutParams);
    }

    private void notMyMessageView(ChatHolder holder, LinearLayout.LayoutParams layoutParams){
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.LEFT;
        layoutParams.topMargin = 24;
        holder.chatSenderTxtView.setLayoutParams(layoutParams);
        holder.chatTxtView.setLayoutParams(layoutParams);
        holder.chatTimeTxtView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return baseMessageList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        TextView chatTxtView, chatTimeTxtView, chatSenderTxtView, chatDateTxtView;

        public ChatHolder(View itemView) {
            super(itemView);
            chatSenderTxtView = itemView.findViewById(R.id.chatSenderTxtView);
            chatTxtView = itemView.findViewById(R.id.chatTxtView);
            chatTimeTxtView = itemView.findViewById(R.id.chatTImeTxtView);
            chatDateTxtView = itemView.findViewById(R.id.chatDateTxtView);
        }
    }
}
