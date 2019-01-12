package com.gandan.android.sendbirdtest.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gandan.android.sendbirdtest.Activity.ActivityChat;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBirdException;

import java.util.List;

public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelRecyclerHolder> {

    Context context;
    List<BaseChannel> baseChannelList;
    String userId = "";
    int size = 0;
    AlertDialog alertDialog;
    DeleteListener deleteListener;

    public interface DeleteListener {
        void onDelete();
    }

    public ChannelRecyclerAdapter(Context context, List<BaseChannel> baseChannelList, String userId, DeleteListener deleteListener){
        this.context = context;
        this.baseChannelList = baseChannelList;
        this.size = baseChannelList.size();
        this.userId = userId;
        this.deleteListener = deleteListener;
    }


    @NonNull
    @Override
    public ChannelRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ChannelRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelRecyclerHolder holder, int position) {
        final BaseChannel baseChannel = baseChannelList.get(position);
        holder.text1.setText(baseChannel.getName());
        holder.text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(baseChannel.isOpenChannel()){
                    Intent intent = new Intent(context, ActivityChat.class);
                    intent.putExtra("type", "open");
                    intent.putExtra("chatUrl", baseChannel.getUrl()+"");
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                } else if (baseChannel.isGroupChannel()){
                    Intent intent = new Intent(context, ActivityChat.class);
                    intent.putExtra("type", "group");
                    intent.putExtra("chatUrl", baseChannel.getUrl()+"");
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "What the...?!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.text1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(baseChannel.isOpenChannel()){
                    final OpenChannel openChannel = (OpenChannel) baseChannel;
                    alertDialog = new AlertDialog.Builder(context).setTitle("Delete room "+openChannel.getName()+"?").setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRoom(openChannel);
                        }
                    }).create();
                    alertDialog.show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return size;
    }

    private void deleteRoom(OpenChannel openChannel){
        openChannel.delete(new OpenChannel.OpenChannelDeleteHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if( e == null){
                    deleteListener.onDelete();
                    if(alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                } else {
                    Log.e("Error", e.getMessage()+"");

                }
            }
        });
    }

    class ChannelRecyclerHolder extends RecyclerView.ViewHolder {

        TextView text1;

        public ChannelRecyclerHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
