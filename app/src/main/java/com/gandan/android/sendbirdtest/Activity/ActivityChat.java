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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        type = getIntent().getStringExtra("type");
        chatUrl = getIntent().getStringExtra("chatUrl");
        userId = getIntent().getStringExtra("userId");
        actionBar = getSupportActionBar();
        chatRecycler = findViewById(R.id.chatRecycler);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerAdapter = new ChatRecyclerAdapter(this, messageList);
        chatRecycler.setAdapter(chatRecyclerAdapter);
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        msgEditText = findViewById(R.id.msgEditText);

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if( e == null ){
                    
                } else {
                    Log.e("e", e.getMessage()+"");
                }
            }
        });

        if(!"".equals(type) && !"".equals(chatUrl)){
            switch(type){
                case "open":
                    OpenChannel.getChannel(chatUrl, new OpenChannel.OpenChannelGetHandler() {
                        @Override
                        public void onResult(OpenChannel openChannel, SendBirdException e) {
                            if( e == null){
                                baseChannel = openChannel;
                                Toast.makeText(ActivityChat.this, "Connected OpenChat", Toast.LENGTH_SHORT).show();
                                actionBar.setTitle(openChannel.getName());
                                loadMessages();
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
                                Toast.makeText(ActivityChat.this, "Connected GroupChat", Toast.LENGTH_SHORT).show();
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
        previousMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                for(BaseMessage msg : list){
                    messageList.add(msg);
                }
                chatRecyclerAdapter.notifyDataSetChanged();
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
                            loadMessages();
                        } else {
                            Log.e("E", e.getMessage()+"");
                        }
                    }
                });
                break;
        }
    }
}