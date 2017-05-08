package com.example.vikram.charla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name, email, password;
    private Button signupbtn;
    private TextView logintxt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.uname);
        email = (EditText) findViewById(R.id.uemail);
        password = (EditText) findViewById(R.id.upswd);
        signupbtn = (Button) findViewById(R.id.signup);
        logintxt = (TextView) findViewById(R.id.logintxt);


        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();//initialize auth object

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }

        signupbtn.setOnClickListener(this);
        logintxt.setOnClickListener(this);


    }

    private void registeruser() {

        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) { //check for empty email

            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;                                //stop  function execution
        }

        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {                             //user registered successfully
                            progressDialog.dismiss();

                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            // Toast.makeText(SignUp.this, "Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {

        if (v == signupbtn) { //verify registering of user

            registeruser();
        }

        if (v == logintxt) { //goto login activity
            // finish();

         Intent i =new Intent(this,Username.class);

            startActivity(i);
        }



    }
}
