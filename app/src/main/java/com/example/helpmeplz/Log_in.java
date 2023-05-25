package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Log_in extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    EditText editText_email,editText_Password;
    Button login_btn, signup_btn;
    Button test_btn;
    SignInButton google_login_btn;

    FirebaseAuth mAuth=null;
    private static final int RC_SIGN_IN = 9001;
    private static final int REQ_ONE_TAP=2;
    private boolean showOneTapUI=true;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;

    GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        mAuth=FirebaseAuth.getInstance();

        signup_btn = findViewById(R.id.button_sign_in);
        editText_email= findViewById(R.id.editText_email);
        editText_Password= findViewById(R.id.editText_Password);

        test_btn= findViewById(R.id.test_button);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);



        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //default_web_client_id
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);*/

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Menu.class);
                startActivity(intent);
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Sign_in.class);
                startActivity(intent);
            }
        });

        login_btn= findViewById(R.id.button_login);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_email.getText().toString().equals("") && !editText_Password.getText().toString().equals("")) {
                    loginUser(editText_email.getText().toString(), editText_Password.getText().toString());
                } else {
                    Toast.makeText(Log_in.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Log_in.this, Menu.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };

        google_login_btn= findViewById(R.id.button_google_login);
        google_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



    }
    private void signIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(),account);
                Log.d("google", "firebaseAuthWithGoogle:" + account.getId());
            }catch(ApiException e){
                Log.w("google", "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken,GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Log_in.this,"구글 로그인 성공",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),Menu.class);
                            intent.putExtra("UserName", account.getDisplayName());
                            intent.putExtra("PhotoUrl", account.getPhotoUrl());
                            startActivity(intent);
                        }else{
                            Log.w("google", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Log_in.this,"구글 로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /*protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_ONE_TAP:
                try{
                    SignInCredential credential=oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken=credential.getGoogleIdToken();
                    if(idToken!=null){
                        Log.d("TAG","Got ID token.");
                        AuthCredential firebaseCredential= GoogleAuthProvider.getCredential(idToken,null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("TAG","signInWithCredential:onComplete:"+task.isSuccessful());
                                        if(task.isSuccessful()){
                                            FirebaseUser user=mAuth.getCurrentUser();
                                            Log.d("TAG","display name:"+user.getDisplayName());
                                            Log.d("TAG","email:"+user.getEmail());
                                            Log.d("TAG","photo url:"+user.getPhotoUrl());
                                            updateUI(user);
                                        }else{
                                            Log.w("TAG","signInWithCredential",task.getException());
                                            Toast.makeText(Log_in.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }
                                    }
                                });
                    }
                }catch (ApiException e){
                    e.printStackTrace();
                    Log.d("TAG","One-tap sign-in failed:"+e.getMessage());
                }
        }
    }*/

    /*private void signIn(){
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }*/

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            Intent intent=new Intent(this,Menu.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show();
        }
    }


    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(com.example.helpmeplz.Log_in.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        } else {
                            // 로그인 실패
                            Toast.makeText(com.example.helpmeplz.Log_in.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}