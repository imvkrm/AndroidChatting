package com.example.vikram.charla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Username extends AppCompatActivity implements View.OnClickListener{

    private EditText uname;

    private SharedPreferences sharedPreferences;
    private Button nxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);



        sharedPreferences = getSharedPreferences("myuname",MODE_PRIVATE);

        uname =(EditText)findViewById(R.id.usrname);

        nxt=(Button)findViewById(R.id.nxt);


        nxt.setOnClickListener(this);
        if(sharedPreferences.contains("uname"))
        {
            uname.setText(sharedPreferences.getString("uname",""));

        }


    }

    @Override
    public void onClick(View v) {

        SharedPreferences.Editor editor=sharedPreferences.edit();
        String user;
        user = uname.getText().toString();
        editor.putString("uname",user);
        editor.commit();

        String username = uname.getText().toString();
        Intent i =new Intent(this,MainActivity.class);
        i.putExtra("uname",username);
        startActivity(i);



    }
}
