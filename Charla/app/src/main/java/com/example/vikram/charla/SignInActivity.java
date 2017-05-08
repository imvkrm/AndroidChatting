package com.example.vikram.charla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar  msignintoolbar;
    private EditText email, password;
    private Button loginbtn;
    private TextView signuptxt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private final static int RC_SIGN_IN =1; //assign value for google signin
    private GoogleApiClient mGoogleApiClient;
    private static final  String TAG = "LOGIN";
    private FirebaseAuth.AuthStateListener authStateListener;
    private SignInButton googlebutton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);




          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }*/
            email=(EditText)findViewById(R.id.email);
            password=(EditText)findViewById(R.id.password);
            loginbtn=(Button)findViewById(R.id.Logbtn);
            signuptxt=(TextView)findViewById(R.id.signuptxt);
            googlebutton = (SignInButton) findViewById(R.id.googlebtn);

            firebaseAuth= FirebaseAuth.getInstance();
            progressDialog=new ProgressDialog(this);

            if(firebaseAuth.getCurrentUser()!= null){//user is already logged in
                finish();

                Intent i =new Intent(this,Username.class);
                startActivity(i);

            }

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginuser();
                }
            });
            signuptxt.setOnClickListener(this);


            authStateListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser()!= null){
                        progressDialog.setMessage("Login....");
                        progressDialog.show();
                        finish();
                        startActivity(new Intent(getApplicationContext(),Username.class));
                    }
                }
            };


            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient= new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage( this, new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(SignInActivity.this, "Error in Signin ", Toast.LENGTH_SHORT).show();

                                }
                            })

                    .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .build();
            googlebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();   //call google signin fn below
                }
            });
        }


    private void loginuser() {

        String Email= email.getText().toString().trim();
        String Password =password.getText().toString().trim();

        if(TextUtils.isEmpty(Email)){ //check for empty email

            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;                                //stop  function execution
        }

        if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Login....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            //goto main Activity
                            finish();

                            Intent i =new Intent(getApplicationContext(),Username.class);
                            startActivity(i);

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {


        if(v == signuptxt ){
            finish();
            startActivity(new Intent(this,RegisterActivity.class));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {

            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void signIn() {   //google signin

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressDialog.setMessage("Login....");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        //  progressDialog.setMessage("Registering....");
                        //  progressDialog.show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

}



