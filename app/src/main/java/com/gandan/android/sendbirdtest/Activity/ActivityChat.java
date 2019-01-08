package com.gandan.android.sendbirdtest.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gandan.android.sendbirdtest.Adapter.ChatRecyclerAdapter;
import com.gandan.android.sendbirdtest.R;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class ActivityChat extends AppCompatActivity implements View.OnClickListener {

    String type = "";
    String chatUrl = "";
    ActionBar actionBar;
    RecyclerView chatRecycler;
    ChatRecyclerAdapter chatRecyclerAdapter;
    BaseChannel baseChannel;
    List<BaseMessage> messageList = new ArrayList<>();
    Button sendBtn;
    EditText msgEditText;
    String userId = "";
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        type = getIntent().getStringExtra("type");
        chatUrl = getIntent().getStringExtra("chatUrl");
        userId = getIntent().getStringExtra("userId");
        actionBar = getSupportActionBar();
        chatRecycler = findViewById(R.id.chatRecycler);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatRecycler.setLayoutManager(linearLayoutManager);
        chatRecyclerAdapter = new ChatRecyclerAdapter(this, messageList);
        chatRecycler.setAdapter(chatRecyclerAdapter);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        msgEditText = findViewById(R.id.msgEditText);

        chatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == messageList.size()-1){
                    loadPreviousMessages();
                }
            }
        });

        /*SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if( e == null ){

                } else {
                    Log.e("e", e.getMessage()+"");
                }
            }
        });*/

                Log.e("type", type + "");
        Log.e("chatUrl", chatUrl+"");

        if(!"".equals(type) && !"".equals(chatUrl)){
            Log.e("type", type+"");
            switch(type){
                case "open":
                    OpenChannel.getChannel(chatUrl, new OpenChannel.OpenChannelGetHandler() {
                        @Override
                        public void onResult(final OpenChannel openChannel, SendBirdException e) {
                            if( e == null){
                                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                                    @Override
                                    public void onResult(SendBirdException e) {
                                        if(e == null){
                                            baseChannel = openChannel;
                                            Toast.makeText(ActivityChat.this, "Connected OpenChat", Toast.LENGTH_SHORT).show();
                                            actionBar.setTitle(baseChannel.getName());
                                            loadMessages();
                                        } else {
                                            Log.e("Err on open", e.getMessage()+"");
                                        }
                                    }
                                });

                            } else {
                                Log.e("error enterOpen", e.getMessage()+"");
                            }
                        }
                    });
                    break;
                case "group":
                    GroupChannel.getChannel(chatUrl, new GroupChannel.GroupChannelGetHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if( e == null){
                                baseChannel = groupChannel;
                                Toast.makeText(ActivityChat.this, groupChannel.getMemberCount()+"", Toast.LENGTH_SHORT).show();
                                actionBar.setTitle(groupChannel.getName());
                                loadMessages();
                            } else {
                                Log.e("error enterGrp", e.getMessage()+"");
                            }
                        }
                    });
                    break;
            }
        }


    }

    private void loadMessages(){
        PreviousMessageListQuery previousMessageListQuery = baseChannel.createPreviousMessageListQuery();
        previousMessageListQuery.load(5, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e == null) {
                    for (BaseMessage msg : list) {
                        messageList.add(msg);
                        }
                        Log.e("messageSIze", messageList.size()+"");
                        chatRecyclerAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("loadError", e.getMessage() + "");
                    }
                }
            });
    }

    private void loadPreviousMessages() {
        UserMessage ms = (UserMessage) messageList.get(0);
        Log.e("messageTime", ms.getMessage()+"");
        baseChannel.getPreviousMessagesByTimestamp(messageList.get(0).getCreatedAt(), false, 1, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e == null) {
                    for (BaseMessage msg : list) {
                        messageList.add(msg);
                    }
                    chatRecyclerAdapter.notifyDataSetChanged();
                } else {
                    Log.e("loadERR", e.getMessage()+"");
                }
            }
        });
    }

    private void newMessage(){
        baseChannel.getNextMessagesByTimestamp(messageList.get(0).getCreatedAt(), false, 1, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e == null){
                    for ( BaseMessage msg : list){
                        messageList.add(0, msg);
                    }
                    chatRecyclerAdapter.notifyDataSetChanged();
                } else {
                    Log.e("newE", e.getMessage()+"");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendBtn:
                baseChannel.sendUserMessage(msgEditText.getText().toString(), new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if(e == null){
                            newMessage();
                            Toast.makeText(ActivityChat.this, "Sended!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("E", e.getMessage()+"");
                        }
                    }
                });
                break;
        }
    }
}
