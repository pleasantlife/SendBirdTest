package com.gandan.android.sendbirdtest.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gandan.android.sendbirdtest.Adapter.ParticipantRecyclerAdapter;
import com.gandan.android.sendbirdtest.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelMemberListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.ParticipantListQuery;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;

import java.util.ArrayList;
import java.util.List;

public class ActivityParticipant extends AppCompatActivity {


    RecyclerView recyclerParticipant;
    ParticipantRecyclerAdapter participantRecyclerAdapter;
    String chatUrl = "";
    String type = "";
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        chatUrl = getIntent().getStringExtra("chatUrl");
        type = getIntent().getStringExtra("type");

        recyclerParticipant = findViewById(R.id.recyclerParticipant);
        recyclerParticipant.setLayoutManager(new LinearLayoutManager(this));
        participantRecyclerAdapter = new ParticipantRecyclerAdapter(this, userList);


        if(!"".equals(chatUrl)){
            switch(type){
                case "open":
                    OpenChannel.getChannel(chatUrl, new OpenChannel.OpenChannelGetHandler() {
                        @Override
                        public void onResult(OpenChannel openChannel, SendBirdException e) {
                            if( e == null){
                                ParticipantListQuery participantListQuery = openChannel.createParticipantListQuery();
                                participantListQuery.next(new UserListQuery.UserListQueryResultHandler() {
                                    @Override
                                    public void onResult(List<User> list, SendBirdException e) {
                                        if (e == null){
                                            for(User user : list){
                                                userList.add(user);
                                                Log.e("participant", user.getUserId()+"");
                                            }
                                            participantRecyclerAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("errorParti", e.getMessage()+"");
                                        }
                                    }
                                });
                            } else {
                                Log.e("ErrorParti", e.getMessage()+"");
                            }
                        }
                    });
                    break;
                case "group":
                    GroupChannel.getChannel(chatUrl, new GroupChannel.GroupChannelGetHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if (e == null){
                                GroupChannelMemberListQuery memberListQuery = groupChannel.createMemberListQuery();
                                memberListQuery.next(new GroupChannelMemberListQuery.GroupChannelMemberListQueryResultHandler() {
                                    @Override
                                    public void onResult(List<Member> list, SendBirdException e) {
                                        if(e == null){
                                            for(Member member : list){
                                                Log.e("member", member.getUserId()+"");
                                            }
                                        } else {
                                            Log.e("errorG", e.getMessage()+"");
                                        }
                                    }
                                });


                            }
                        }
                    });
                    break;
            }

        }


    }
}
