package com.hufsice.nulltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hufsice.nulltime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_in extends AppCompatActivity {
    Button signin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signin = findViewById(R.id.button_sign_in);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signin.setOnClickListener(new Button.OnClickListener() {//회원가입 버튼 함수 호출하는 것
            @Override
            public void onClick(android.view.View view) {
                signUp();
            }
        });

    }
    private void signUp(){//사용자가 입력한 정보들을 서버에 저장하고 데이터베이스에 항목생성
        String name=((EditText)findViewById(R.id.editText_name)).getText().toString();
        String email=((EditText)findViewById(R.id.editText_id)).getText().toString();
        String password=((EditText)findViewById(R.id.editText_password)).getText().toString();
        String password_confirm=((EditText)findViewById(R.id.editText_password_confirm)).getText().toString();

        if(email.length()>0 && password.length()>0 && password_confirm.length()>0){
            if(password.equals(password_confirm)){
                //firestore에 email,password 저장
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원가입에 성공하면 user변수에 추가
                                    FirebaseUser user=mAuth.getCurrentUser();
                                    if(user!=null){
                                        String uid=user.getUid();
                                        mDatabase.child("users").child(uid).child("name").setValue(name);
                                        mDatabase.child("users").child(uid).child("email").setValue(email);
                                        mDatabase.child("search").child(name).child(uid).setValue("");
                                    }
                                    Log.d("TAG", "createUserWithEmail:success");
                                    Toast.makeText(Sign_in.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    // 가입에 실패하면 사용자에게 표시
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(Sign_in.this, "회원가입 실패,이메일의 형식과 비밀번호가 6자리이상인지 확인해주세요",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else{
                Toast.makeText(Sign_in.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Sign_in.this, "아이디와 비밀번호를 확인해주세요." ,Toast.LENGTH_SHORT).show();
        }
    }


}