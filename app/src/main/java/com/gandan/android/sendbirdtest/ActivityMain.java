package com.gandan.android.sendbirdtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gandan.android.sendbirdtest.Adapter.ChannelRecyclerAdapter;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    EditText nameEditText;
    Button editNameBtn;
    List<GroupChannel> groupChannelList;
    List<OpenChannel> openChannelList;
    ChannelRecyclerAdapter channelRecyclerAdapter;
    RecyclerView channelRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channelRecyclerView = findViewById(R.id.channelRecyclerView);

        SendBird.init(getString(R.string.sendbird_app_id), this);

        nameEditText = findViewById(R.id.nameEditText);
        if("".equals(nameEditText.getText().toString())) {
            nameEditText.setEnabled(true);
        } else {
            nameEditText.setEnabled(false);
        }
        editNameBtn = findViewById(R.id.editNameBtn);

        editNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.isEnabled()){
                    nameEditText.setEnabled(false);
                    connectSendBird(nameEditText.getText().toString());
                } else {
                    nameEditText.setEnabled(true);
                }
            }
        });


    }

    private void makeGroupChannel(User user){
        List<User> userList = new ArrayList<>();
        userList.add(user);
        GroupChannel.createChannel(userList, false, "Hello Group", null, null, null, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if( e == null){
                    getGroupChannelList();
                } else {
                    Log.e("makeError", e.getMessage()+"");
                }
            }
        });
    }

    private void connectSendBird(String userId){
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if( e == null) {
                    Toast.makeText(ActivityMain.this, user.getUserId() + "", Toast.LENGTH_SHORT).show();
                    makeGroupChannel(user);
                } else {
                    Log.e("SendBirdError", e.getMessage()+"");
                }
            }
        });
    }

    private void getGroupChannelList(){
        GroupChannelListQuery groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        groupChannelListQuery.setIncludeEmpty(true);
        groupChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e == null){
                    groupChannelList = list;
                    getOpenChannelList();
                } else {
                    Log.e("GroupError", e.getMessage()+"");
                }
            }
        });
    }

    private void getOpenChannelList(){
        OpenChannelListQuery openChannelListQuery = OpenChannel.createOpenChannelListQuery();
        openChannelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if( e == null){
                    openChannelList = list;
                    setRecyclerView();
                } else {
                    Log.e("OpenError", e.getMessage()+"");
                }
            }
        });
    }

    private void setRecyclerView(){
        channelRecyclerAdapter = new ChannelRecyclerAdapter(this, groupChannelList, openChannelList);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        channelRecyclerView.setAdapter(channelRecyclerAdapter);
        channelRecyclerAdapter.notifyDataSetChanged();
    }


}
