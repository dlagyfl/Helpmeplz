package com.hufsice.nulltime;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFriend extends AppCompatActivity {
    private String friendId;
    private String friendName;
    private String userId;
    private String userName;

    private DatabaseReference database;
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //textview, button, 파이어베이스 데이터베이스 변수선언
        view = findViewById(R.id.textView_friend_list);
        Button button_Accept = findViewById(R.id.button_accept);
        Button button_Refuse = findViewById(R.id.button_refuse);
        database = FirebaseDatabase.getInstance().getReference();

        //로그인 되어있는 유저의 UID를 받아옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = user.getUid();
            Log.d("uid", userId);
        }

        //데이터베이스에 등록된 그룹의 이름을 DataSnapshot을 이용하여 받아옴
        database.child("users").child(userId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + (String) dataSnapshot.getValue());
                userName = (String) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//실패할경우 로그에 에러문 출력
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });

        //친구정보를 등록하는 버튼 이벤트 함수
        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "onCreate - onClick : " + friendId);
                // 친구 DB에 내 UID 넣기
                database.child("users").child(friendId).child("friendlist").child(userId).child("name").setValue(userName);
                // 내 DB에 친구 UID 넣기
                database.child("users").child(userId).child("friendlist").child(friendId).child("name").setValue(friendName);
                // 내 DB의 friendrequest에서 친구 추가한 UID 삭제하기
                Log.d("MainActivity", "onCreate - onClick : " + 1);
                //데이터베이스랑 통신중 버퍼가 조과되는 문제가 발생해 의도적으로 잠시 기다림
                sleep(500);
                //DB에서 신청 삭제
                database.getRef().child("users").child(userId).child("friendrequest").child(friendId).removeValue();
                Log.d("MainActivity", "onCreate - onClick : " + 2);
                Log.d("MainActivity", "onCreate - onClick : " + friendName + " before intent");
                //친구 추가가 완료되면 addFriendComplete로 이동하여 친구추가완료를 안내
                Intent intent = new Intent(AddFriend.this, AddFriendComplete.class);
                intent.putExtra("nickname", friendName);
                startActivity(intent);
                database.getRef().child("users").child(userId).child("friendrequest").child(friendId).removeValue();
                finish();
                Log.d("MainActivity", "onCreate - onClick : " + friendName + " after intent");
            }
        });

        //친구 신청 거절의 버튼 이벤트 함수
        button_Refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB의 친구 신청 삭제
                database.child("users").child(userId).child("friendrequest").child(friendId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                //전의 엑티비티로 돌아감
                Intent myIntent2 = new Intent(AddFriend.this, MyFriend.class);
                startActivity(myIntent2);
                finish();
            }
        });

        getUID();
    }

    //DB에서 친구라스트를 받아옴
    private void getUID() {

        database.child("users").child(userId).child("friendrequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //여러명인 친구를 받아오기 위한 반복문
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendId = snapshot.getKey();
                    Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId);
                }
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId);
                //친구의 UID를 가지고 친구의 닉네임을 가져옴
                getName(friendId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }

    //DB에서 친구의 UID를 이용하여 친구의 이름을 가져옴
    private void getName(String UID) {
        database.child("users").child(UID).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//친구의 이름을 가져와서 출력
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + (String) dataSnapshot.getValue());

                friendName = (String) dataSnapshot.getValue();

                view.setText(friendName + "님께서");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//실패시 에러메세지 출력
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }
}
