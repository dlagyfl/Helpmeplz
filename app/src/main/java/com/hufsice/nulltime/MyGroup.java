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
    private Button find_null_time;
    private ListView listView_member;
    private TextView txtView_groupName;
    private ArrayAdapter<Object> groupListAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String groupId;
    private ImageButton imbtn_exit;
    List<String> memberList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        find_null_time = findViewById(R.id.find_null_time);
        listView_member = findViewById(R.id.listView_member);
        txtView_groupName = findViewById(R.id.txtView_groupName);
        imbtn_exit = findViewById(R.id.imbtn_exit);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        String groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId"); //groupId 가지고 오기

        groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        getGroupMember(groupId);
        listView_member.setAdapter(groupListAdapter);

        txtView_groupName.setText(groupName);

        Intent intent1 = getIntent();
        String groupID = intent1.getStringExtra("groupId");
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("groups").child("users").child(userId).child(groupID).child("members");

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    String memberId = memberSnapshot.getKey();
                    memberList.add(memberId);
                }

                findNullTime();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
                Log.e("Firebase", "Error fetching members: " + databaseError.getMessage());
            }
        });

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

    private void getGroupMember(String groupId) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        reference.child("groups").child("users").child(userId).child(groupId).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> members = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);
                    members.add(member.getName());
                }
                groupListAdapter.clear();
                groupListAdapter.addAll(members);
                groupListAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }

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

    private void showLeaveGroupConfirmation() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String groupId = getIntent().getStringExtra("groupId");

        DatabaseReference hostRef = reference.child("groups").child("users").child(userId).child(groupId).child("host");
        hostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String host = dataSnapshot.getValue(String.class);

                if(host != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyGroup.this);
                    if (host.equals(userId)) {
                        builder.setMessage("그룹을 삭제하시겠습니까?")
                                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteGroup(groupId);
                                        dialog.dismiss(); // 대화상자 닫기
                                        Toast.makeText(getApplicationContext(), "그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MyGroup.this, GroupList.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        builder.setMessage("그룹을 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(getApplicationContext(), "방장만 사용할 수 있는 기능입니다.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss(); // 대화상자 닫기
                                    }
                                });
                        builder.create().show();
                    }
                } else {
                    //몰라~~
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기가 실패한 경우 호출되는 메서드
                Log.e("Firebase", "Error fetching host: " + databaseError.getMessage());
            }
        });
    }

    private void deleteGroup(String groupId) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        reference.child("groups").child("users").child(userId).child(groupId).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });

    }
}