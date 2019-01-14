package com.gandan.android.sendbirdtest.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.sendbird.android.ParticipantListQuery;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
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
    boolean isLoading = false;


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
        chatRecyclerAdapter = new ChatRecyclerAdapter(this, messageList, userId);
        chatRecycler.setAdapter(chatRecyclerAdapter);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        msgEditText = findViewById(R.id.msgEditText);

        chatRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == messageList.size()-1 && !isLoading && messageList.size() > 0){
                    isLoading = true;
                    loadPreviousMessages(messageList.size()-1);
                }
            }
        });

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
                                            loadFirstMessages();
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
                                loadFirstMessages();
                            } else {
                                Log.e("error enterGrp", e.getMessage()+"");
                            }
                        }
                    });
                    break;
            }
        }


        //Receive new Message and locate to 0 in list
        SendBird.addChannelHandler(userId, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                messageList.add(0, baseMessage);
                chatRecyclerAdapter.notifyDataSetChanged();
            }
        });

    }

    private void loadFirstMessages(){
        PreviousMessageListQuery previousMessageListQuery = baseChannel.createPreviousMessageListQuery();
        previousMessageListQuery.load(10, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e == null) {
                    for (BaseMessage msg : list) {
                        messageList.add(msg);
                        }
                        Log.e("messageSIze", messageList.size()+"");
                    } else {
                        Log.e("loadError", e.getMessage() + "");
                    }
                chatRecyclerAdapter.notifyDataSetChanged();
                }
            });
    }

    private void loadPreviousMessages(int number) {
        UserMessage ms = (UserMessage) messageList.get(0);
        Log.e("messageTime", ms.getMessage()+"");
        baseChannel.getPreviousMessagesByTimestamp(messageList.get(number).getCreatedAt(), false, 10, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e == null) {
                    for (BaseMessage msg : list) {
                        messageList.add(msg);
                    }
                    chatRecyclerAdapter.notifyDataSetChanged();
                    isLoading = false;
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
                            msgEditText.setText("");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatOutMenu:
                exitChat();
                return true;
            case R.id.chatMemberMenu:
                goMemberMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void exitChat(){
        switch(type){
            case "open":
                OpenChannel.getChannel(chatUrl, new OpenChannel.OpenChannelGetHandler() {
                    @Override
                    public void onResult(OpenChannel openChannel, SendBirdException e) {
                        if(e != null){
                            Log.e("err", e.getMessage()+"");
                        }

                        openChannel.exit(new OpenChannel.OpenChannelExitHandler() {
                            @Override
                            public void onResult(SendBirdException e) {
                                if( e == null){
                                    Toast.makeText(getApplicationContext(), "Exit!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Log.e("Error", e.getMessage()+"");
                                }
                            }
                        });
                    }
                });
        }
    }

    private void goMemberMenu(){
        Intent intent = new Intent(this, ActivityParticipant.class);
        intent.putExtra("chatUrl", chatUrl);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
