package com.example.vikram.charla;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Chat_Room  extends AppCompatActivity{

    private ImageButton btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private ScrollView scrollView;
    private LinearLayout layout;

    private String user_name,room_name;
    private DatabaseReference root ;
    private String temp_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        layout = (LinearLayout)findViewById(R.id.layout1);
        scrollView=(ScrollView)findViewById(R.id.scroll);
        btn_send_msg = (ImageButton) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);

        user_name = getIntent().getStringExtra("user_name");
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg",input_msg.getText().toString());

                message_root.updateChildren(map2);
                input_msg.setText("");
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String chat_msg,chat_user_name;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        chat_conversation = new TextView(Chat_Room.this);
        //chat_conversation.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 15);
        chat_conversation.setLayoutParams(lp);
        chat_conversation.setTextSize(15);

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){
           // SpannableStringBuilder builder = new SpannableStringBuilder();

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            chat_conversation.append(chat_user_name +"\n"+"--------------------------------------"+ "\n  "+chat_msg );
          /*  SpannableString txtSpannable= new SpannableString(chat_msg);
            txtSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, chat_msg.length(), 0);

            SpannableString nameSpannable= new SpannableString(chat_user_name);
            nameSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, chat_user_name.length(), 0);*/
            //chat_conversation.setBackground(getColor(this,R.color.colorPrimary));

           // chat_conversation.setText(builder, TextView.BufferType.SPANNABLE);
        }
        chat_conversation.setBackgroundResource(R.drawable.rounded_corner1);

       // chat_conversation.setTextColor(getResources().getColor(R.color.textcolor));
        layout.addView(chat_conversation);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

}
