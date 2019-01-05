package com.gandan.android.sendbirdtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gandan.android.sendbirdtest.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.BaseMessageParams;
import com.sendbird.android.UserMessage;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        BaseMessage baseMessage = baseMessageList.get(position);
        if(baseMessage.getMentionType().equals(BaseMessageParams.MentionType.USERS)){
            UserMessage msg = (UserMessage) baseMessage;
            holder.chatTxtView.setText(msg.getMessage()+"");
        }
    }

    @Override
    public int getItemCount() {
        return baseMessageList.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        TextView chatTxtView;

        public ChatHolder(View itemView) {
            super(itemView);
            chatTxtView = itemView.findViewById(R.id.chatTxtView);
        }
    }
}
