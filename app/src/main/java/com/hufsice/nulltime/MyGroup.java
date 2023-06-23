package com.hufsice.nulltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hufsice.nulltime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyGroup extends AppCompatActivity {
    //레이아웃에서 사용하는 뷰 요소들의 변수 선언
    private Button find_null_time;
    private ListView listView_member;
    private TextView txtView_groupName;
    private ImageButton imbtn_exit;
    //Firebase 관련 객체들의 변수 선언
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private ArrayAdapter<Object> groupListAdapter;
    private String groupId;
    List<String> memberList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        //변수 연결
        find_null_time = findViewById(R.id.find_null_time);
        listView_member = findViewById(R.id.listView_member);
        txtView_groupName = findViewById(R.id.txtView_groupName);
        imbtn_exit = findViewById(R.id.imbtn_exit);
        //Firebase 실시간 데이터베이스와 Firebase 인증을 사용하기 위해 필요한 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        // GroupList activity에서 보낸 그룹명과 그룹 id 받아오기
        String groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

        groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        //그룹 멤버 정보 가져오기
        getGroupMember(groupId);
        listView_member.setAdapter(groupListAdapter);

        //groupList activity에서 선택한 그룹에 대한 이름 textView로 보여주기
        txtView_groupName.setText(groupName);

        //현재 로그인한 사용자 id userId에 저장
        String userId = firebaseAuth.getCurrentUser().getUid();
        //그룹 멤버에 대해 접근, datasnapshot으로 멤버 정보 읽기
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("groups").child("users").child(userId).child(groupId).child("members");
        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    String memberId = memberSnapshot.getKey();
                    memberList.add(memberId);
                }
                //Null 시간 찾기 기능 실행
                findNullTime();

            }

            //데어터 읽기 실패시에 오류 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
                Log.e("Firebase", databaseError.getMessage());
            }
        });

        //그룹 삭제 여부 묻기 버튼
        imbtn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaveGroupConfirmation();
            }
        });



//        find_null_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyGroup.this, FindNullTime.class);
//                intent.putExtra("memberList", memberList);
//                startActivity(intent);
//            }
//        });
    }

    //그룹정보에 저장되어 있는 멤버 정보 읽기 메소드
    private void getGroupMember(String groupId) {
        //현재 로그인되어 있는 사용자 id를 userId에 저장
        String userId = firebaseAuth.getCurrentUser().getUid();
        //그룹 멤버 정보에 접근
        reference.child("groups").child("users").child(userId).child(groupId).child("members").addValueEventListener(new ValueEventListener() {
            //datasnapshot으로 멤버 정보 읽기
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //멤버 정보 저장 list
                List<Object> members = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //멤버 정보 member에 저장
                    Member member = snapshot.getValue(Member.class);
                    //멤버 list에 반복으로 모든 멤버 이름 저장
                    members.add(member.getName());
                }
                //ArrayAdapter에 멤버 리스트 설정 및 갱신
                groupListAdapter.clear();
                groupListAdapter.addAll(members);
                groupListAdapter.notifyDataSetChanged();
            }

            //실패시에 오류발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
            }
        });
    }

    //공강시간 찾기 메소드
    private void findNullTime() {
        find_null_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyGroup.this, FindNullTime.class);
                intent.putExtra("memberList",  (ArrayList<String>) memberList);
                startActivity(intent);
                finish();
            }
        });
    }

    //그룹 삭제 여부 묻기 메소드
    private void showLeaveGroupConfirmation() {
        //현재 로그인한 user id 저장
        String userId = firebaseAuth.getCurrentUser().getUid();
        //방장 정보 가지고 오기(방장만 그룹을 삭제할 수 있다.)
        DatabaseReference hostRef = reference.child("groups").child("users").child(userId).child(groupId).child("host");
        hostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String host = dataSnapshot.getValue(String.class);
                //대화 상자
                AlertDialog.Builder builder = new AlertDialog.Builder(MyGroup.this);
                //방장 정보와 현재 로그인 되어 있는 사용자 정보가 같은 경우에 그룹 삭제 기능 사용가능
                if (host.equals(userId)) {
                    builder.setMessage("그룹을 삭제하시겠습니까?")
                            .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //방정 정보 일치시에 그룹 삭제 메서드 발생
                                    deleteGroup(groupId);
                                    //대화상자 닫기
                                    dialog.dismiss();
                                    //그룹삭제 성공 toast발생
                                    Toast.makeText(getApplicationContext(), "그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    //GroupList acitivity로 intent
                                    Intent intent = new Intent(MyGroup.this, GroupList.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            //'아니요' 선택시 대화상자가 닫히고 어떤 동작도 일어나지 않음
                            .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } //방장 정보와 현재 로그인 정보가 다른 경우 그룹 삭제 기능 사용 불가
                else {
                    builder.setMessage("그룹을 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //방장만 사용할 수 있다는 toast 발생
                                    Toast.makeText(getApplicationContext(), "방장만 사용할 수 있는 기능입니다.", Toast.LENGTH_SHORT).show();
                                    //대화상자 닫기
                                    dialog.dismiss();
                                }
                            })
                            //'아니요' 선택시 대화상자가 닫히고 어떤 동작도 일어나지 않음
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기가 실패한 경우 호출되는 메서드
                Log.e("Firebase",  databaseError.getMessage());
            }
        });
    }

    //그룹 삭제 메소드
    private void deleteGroup(String groupId) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        reference.child("groups").child("users").child(userId).child(groupId).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //DB 탐색하면서 현재 그룹 id 정보 삭제

                    String memberId = snapshot.getKey();
                    DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference();
                    memberRef.child("groups").child("users").child(memberId).child(groupId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 데이터 삭제 성공 시 실행할 코드
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 데이터 삭제 실패 시 실행할 코드
                                }
                            });
                }
            }
            //data 접근 실패시 오류 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
            }
        });

    }
}