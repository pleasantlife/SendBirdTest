package com.gandan.android.sendbirdtest.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gandan.android.sendbirdtest.R;


public class MakeChatDialog extends Dialog {

    public interface OnConfirmClickListener {
        void onConfirm(String roomName);
    }

    OnConfirmClickListener onConfirmClickListener;
    EditText roomNameEditText;
    Button confirmBtn;

    public MakeChatDialog(@NonNull Context context) {
        super(context);
    }

    public MakeChatDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_make_chat);

        roomNameEditText = findViewById(R.id.roomNameEditText);

        confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(roomNameEditText.getText().toString())){
                    Toast.makeText(getContext(), "Enter Room Name", Toast.LENGTH_SHORT).show();
                } else {
                    onConfirmClickListener.onConfirm(roomNameEditText.getText().toString());
                }
            }
        });

    }
}
