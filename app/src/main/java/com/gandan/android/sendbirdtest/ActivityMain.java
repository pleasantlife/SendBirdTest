package com.gandan.android.sendbirdtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.text.SimpleDateFormat;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendBird.init(getString(R.string.sendbird_app_id), this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:dd");
        String userId = sdf.format(System.currentTimeMillis());

        SendBird.connect("2019-01-01 11:27:01", new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if( e == null) {
                    Toast.makeText(ActivityMain.this, user.getUserId() + "", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SendBirdError", e.getMessage()+"");
                }
            }
        });
    }
}
