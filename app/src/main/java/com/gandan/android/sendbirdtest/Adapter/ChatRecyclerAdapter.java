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

        Log.e("message", "Number :"+position + " : " + message.getMessage());
        holder.chatSenderTxtView.setText(message.getSender().getUserId()+"");
        holder.chatTxtView.setText(message.getMessage()+"");
        holder.chatTimeTxtView.setText(sdf.format(message.getCreatedAt()).split(" ")[1]);
        holder.chatDateTxtView.setText(dateFormat.format(message.getCreatedAt()));

        //나의 메세지
        if(message.getSender().getUserId().equals(userid)){
            if(position == baseMessageList.size()-1){
                holder.chatDateTxtView.setVisibility(View.VISIBLE);
            } else {
                UserMessage previousMessage = (UserMessage) baseMessageList.get(position+1);
                //일자가 같을 경우
                if(dateFormat.format(previousMessage.getCreatedAt()).equals(dateFormat.format(message.getCreatedAt()))){
                    holder.chatDateTxtView.setVisibility(View.INVISIBLE);
                }
                //일자가 다를 경우
                else {
                    holder.chatDateTxtView.setVisibility(View.VISIBLE);
                }
            }
            //우측정렬
            lp.gravity = Gravity.RIGHT;
            holder.chatSenderTxtView.setVisibility(View.GONE);
            holder.chatTxtView.setLayoutParams(lp);
            holder.chatTimeTxtView.setLayoutParams(lp);
        }
        //다른 사람의 메세지
        else {
            //(불러온 메세지들 중에) 가장 예전 메세지 ==> 가장 윗 자리를 차지하게 됨.
            if(position == baseMessageList.size()-1){
                holder.chatSenderTxtView.setVisibility(View.VISIBLE);
                holder.chatDateTxtView.setVisibility(View.VISIBLE);
            }
            //그 이외의 메세지 ==> 메세지의 직전 메세지를 확인!
            else {
                UserMessage previousMessage = (UserMessage) baseMessageList.get(position+1);
                String previousId = previousMessage.getSender().getUserId();
                //직전 발신자가 같을 경우
                if(previousId.equals(message.getSender().getUserId())){
                    holder.chatSenderTxtView.setVisibility(View.INVISIBLE);
                }
                //직전 발신자가 다를 경우
                else {
                    holder.chatSenderTxtView.setVisibility(View.VISIBLE);
                }
                //일자가 같을 경우
                if(dateFormat.format(previousMessage.getCreatedAt()).equals(dateFormat.format(message.getCreatedAt()))){
                    holder.chatDateTxtView.setVisibility(View.INVISIBLE);
                }
                //일자가 다를 경우
                else {
                    holder.chatDateTxtView.setVisibility(View.VISIBLE);
                }

            }
            //좌측정렬
            lp.gravity = Gravity.LEFT;
            holder.chatSenderTxtView.setLayoutParams(lp);
            holder.chatTxtView.setLayoutParams(lp);
            holder.chatTimeTxtView.setLayoutParams(lp);
        }
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
