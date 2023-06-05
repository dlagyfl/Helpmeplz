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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Log_in extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    EditText editText_email,editText_Password;
    Button login_btn, signup_btn;
    Button test_btn;
    SignInButton google_login_btn;
    FirebaseAuth mAuth=null;
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        signup_btn = findViewById(R.id.button_sign_in);
        editText_email= findViewById(R.id.editText_email);
        editText_Password= findViewById(R.id.editText_Password);

//        test_btn= findViewById(R.id.test_button);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();//구글로그인을 위한 토큰발급

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

//        test_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),Menu.class);
//                startActivity(intent);
//            }
//        });

        signup_btn.setOnClickListener(new View.OnClickListener() {//회원가입 엑티비티로 이동하는 버튼
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Sign_in.class);
                startActivity(intent);
            }
        });

        login_btn= findViewById(R.id.button_login);

        login_btn.setOnClickListener(new View.OnClickListener() {//로그인버튼 아이디와 비밀번호가 비어있는지 확인하고 로그인함수 호출
            @Override
            public void onClick(View view) {
                if (!editText_email.getText().toString().equals("") && !editText_Password.getText().toString().equals("")) {
                    loginUser(editText_email.getText().toString(), editText_Password.getText().toString());
                } else {
                    Toast.makeText(Log_in.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {//인증정보가 변경돠면(로그인되면) 메뉴엑티비티로 이동
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

        google_login_btn= findViewById(R.id.button_google_login);//구글 로그인을 위한 함수호출
        google_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }
    private void signIn(){//구글로그인 함수 구현
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//구글로그인 함수 구현
        super.onActivityResult(requestCode, resultCode, data);

        //signin함수에서 getSignInIntent()가 실행되서 결과값이 넘어오면 실행되는 함수
        if (requestCode == RC_SIGN_IN) {
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

    private void firebaseAuthWithGoogle(String idToken,GoogleSignInAccount account){//토큰으로 구글로그인 서버와 통신하여 인증
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Log_in.this,"구글 로그인 성공",Toast.LENGTH_SHORT).show();
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("name").setValue(account.getDisplayName());
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(account.getEmail());
                            //데이터베이스에 구글로그인한 유저의 정보추가
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


    public void loginUser(String email, String password) {//일반로그인 처리함수 firebaseAuth의 인증기능을 사용함
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