package com.gandan.android.sendbirdtest.Activity;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gandan.android.sendbirdtest.Adapter.ChannelRecyclerAdapter;
import com.gandan.android.sendbirdtest.Dialog.MakeChatDialog;
import com.gandan.android.sendbirdtest.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    EditText nameEditText;
    Button editNameBtn, makeOpenChatBtn, makeGroupChatBtn;
    List<BaseChannel> channelList = new ArrayList<>();
    ChannelRecyclerAdapter channelRecyclerAdapter;
    RecyclerView channelRecyclerView;
    User connectUser;
    MakeChatDialog makeChatDialog;
    SwipeRefreshLayout swipeList;
    TextView noRoomTxtView;
    ChannelRecyclerAdapter.DeleteListener deleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteListener = new ChannelRecyclerAdapter.DeleteListener() {
            @Override
            public void onDelete() {
                reload();
            }
        };

        noRoomTxtView = findViewById(R.id.noRoomTxtView);
        channelRecyclerView = findViewById(R.id.channelRecyclerView);

        makeOpenChatBtn = findViewById(R.id.makeOpenChatBtn);
        makeGroupChatBtn = findViewById(R.id.makeGroupChatBtn);

        SendBird.init(getString(R.string.sendbird_app_id), this);

        makeChatDialog = new MakeChatDialog(ActivityMain.this);

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
                    getGroupChannelList();
                } else {
                    nameEditText.setEnabled(true);
                }
            }
        });

        swipeList = findViewById(R.id.swipeList);
        if(swipeList.isRefreshing()){
            swipeList.setEnabled(false);
        }

        swipeList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGroupChannelList();
            }
        });



        makeGroupChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeChatDialog.setOnConfirmClickListener(new MakeChatDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirm(String roomName) {
                        makeGroupChannel(connectUser, roomName, null);
                        makeChatDialog.dismiss();
                    }
                });
                makeChatDialog.show();
            }
        });

        makeOpenChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChatDialog.setOnConfirmClickListener(new MakeChatDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirm(String roomName) {
                        makeOpenChannel(connectUser, roomName, null);
                        makeChatDialog.dismiss();
                    }
                });
                makeChatDialog.show();
            }
        });



    }

    private void makeOpenChannel(User user, String roomName, @Nullable String coverImage){
        List<User> userList = new ArrayList<>();
        userList.add(user);
        OpenChannel.createChannel(roomName, null, null, null, userList, new OpenChannel.OpenChannelCreateHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if( e == null){
                    getGroupChannelList();
                } else {
                    Log.e("makeOpenErr", e.getMessage()+"");
                }
            }
        });
    }

    private void reload(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getGroupChannelList();
            }
        });
    }

    private void makeGroupChannel(User user, String roomName, @Nullable String coverImage){
        List<User> userList = new ArrayList<>();
        userList.add(user);
        GroupChannel.createChannel(userList, false, roomName, coverImage, null, null, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if( e == null){
                    getGroupChannelList();
                } else {
                    Log.e("makeGroupErr", e.getMessage()+"");
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
                    connectUser = user;
                } else {
                    Log.e("SendBirdError", e.getMessage()+"");
                }
            }
        });
    }

    private void getGroupChannelList(){
        channelList.clear();
        GroupChannelListQuery groupChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        groupChannelListQuery.setIncludeEmpty(true);
        groupChannelListQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                if (e == null){
                    if(list.size() > 0) {
                        channelList.addAll(list);
                    }
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
                    if(list.size() > 0) {
                        channelList.addAll(list);
                    }
                    setRecyclerView();
                } else {
                    Log.e("OpenError", e.getMessage()+"");
                }
            }
        });
    }

    private void setRecyclerView(){
        channelRecyclerAdapter = new ChannelRecyclerAdapter(this, channelList, connectUser.getUserId(), deleteListener);
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        channelRecyclerView.setAdapter(channelRecyclerAdapter);
        channelRecyclerAdapter.notifyDataSetChanged();
        if(swipeList.isRefreshing()){
            swipeList.setRefreshing(false);
        }
        if( channelList.size() == 0 ) {
            noRoomTxtView.setVisibility(View.VISIBLE);
        } else {
            noRoomTxtView.setVisibility(View.GONE);
        }
    }


}
