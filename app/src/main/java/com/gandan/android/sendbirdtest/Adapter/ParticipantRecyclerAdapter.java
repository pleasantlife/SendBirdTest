package com.gandan.android.sendbirdtest.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendbird.android.User;

import java.util.List;

public class ParticipantRecyclerAdapter extends RecyclerView.Adapter<ParticipantRecyclerAdapter.ParticipantRecyclerHolder> {

    Context context;
    List<User> userList;

    public ParticipantRecyclerAdapter(Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ParticipantRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ParticipantRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantRecyclerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ParticipantRecyclerHolder extends RecyclerView.ViewHolder {

        TextView text1;

        public ParticipantRecyclerHolder(View itemView) {
            super(itemView);

            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}
