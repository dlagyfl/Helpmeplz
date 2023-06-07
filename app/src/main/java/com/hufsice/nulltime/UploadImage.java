package com.hufsice.nulltime;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hufsice.nulltime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImage extends AppCompatActivity {

    // UI 요소 선언
    private ImageView imageView;
    private ProgressBar progressBar;
    private EditText upToTime;

    // Firebase 관련 객체 선언
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // FirebaseApp 초기화
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // UI 요소 초기화
        Button upload_Back = findViewById(R.id.upload_back);
        Button uploadBtn = findViewById(R.id.upload_btn);
        progressBar = findViewById(R.id.progress_View);
        imageView = findViewById(R.id.image_view);
        upToTime = findViewById(R.id.upload_upToTime);
        progressBar.setVisibility(View.INVISIBLE);

        // Firebase 인증 및 데이터베이스 객체 초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 이미지뷰 클릭 시 갤러리에서 이미지 선택
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        // 업로드 버튼 클릭 시 이미지 Firebase에 업로드
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼을 비활성화
                uploadBtn.setEnabled(false);

                // 이미지 URI가 null이 아닌 경우에는 Firebase에 업로드
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    // 이미지가 선택되지 않은 경우, 사용자에게 알림을 표시
                    Toast.makeText(UploadImage.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();

                    // 갤러리 액티비티를 실행하여 이미지를 선택하도록 유도
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/");
                    activityResult.launch(galleryIntent);
                }
            }
        });


        // 뒤로 가기 버튼 클릭 시 메뉴 액티비티로 이동
        upload_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadImage.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 갤러리에서 이미지 선택 후 결과 처리
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // 데이터가 있는 경우에만 실행
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 선택한 이미지의 URI를 가져와서 imageUri 변수에 저장
                        imageUri = result.getData().getData();
                        // 이미지 뷰에 선택한 이미지를 표시
                        imageView.setImageURI(imageUri);
                    }
                }
            });


    // 이미지를 Firebase에 업로드
    private void uploadToFirebase(Uri uri) {
        try {
            Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            int desiredWidth = 0; // 원하는 너비
            int desiredHeight = 0; // 원하는 높이


            int upToTimeValue = Integer.parseInt(upToTime.getText().toString());
            // 각 upToTimeValue 값에 따른 원하는 너비와 높이 설정

            if (upToTimeValue == 1) {
                desiredWidth = 200;
                desiredHeight = 166;
            } else if (upToTimeValue == 2) {
                desiredWidth = 200;
                desiredHeight = 205;
            } else if (upToTimeValue == 3) {
                desiredWidth = 200;
                desiredHeight = 244;
            } else if (upToTimeValue == 4) {
                desiredWidth = 200;
                desiredHeight = 283;
            } else if (upToTimeValue == 5) {
                desiredWidth = 200;
                desiredHeight = 322;
            } else if (upToTimeValue == 6) {
                desiredWidth = 200;
                desiredHeight = 361;
            } else if (upToTimeValue == 7) {
                desiredWidth = 200;
                desiredHeight = 400;
            } else if (upToTimeValue == 8) {
                desiredWidth = 200;
                desiredHeight = 439;
            } else if (upToTimeValue == 9) {
                desiredWidth = 200;
                desiredHeight = 478;
            } else if (upToTimeValue == 10) {
                desiredWidth = 200;
                desiredHeight = 517;
            } else {
                // 위 조건에 충족되지 않는 값을 입력했을 시 메시지를 보여주면서 실행 안됨
                Toast.makeText(UploadImage.this, "유효하지 않는 숫자입니다", Toast.LENGTH_SHORT).show();
                return; // 업로드 중지
            }

            // 원하는 너비와 높이로 이미지 크기 조정
            Bitmap resizedImage = resizeImage(originalImage, desiredWidth, desiredHeight);

            // 이미지 크기 조정 및 테두리 추가
            Bitmap adjustedBitmap = adjustImageSizeAndAddBorder(resizedImage, 200, 517);

            // Bitmap을 JPEG 포맷으로 압축하여 byte 배열로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            adjustedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // 업로드할 파일의 참조 생성
            StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            // Firebase Storage에 파일 업로드
            UploadTask uploadTask = fileRef.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 업로드 성공 후 다운로드 URL 획득
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // 현재 사용자의 UID를 가져와서 데이터베이스에 이미지 URL 저장
                            String userId = mAuth.getCurrentUser().getUid();
                            mDatabase.child("users").child(userId).child("timetable").setValue(imageUrl);

                            // 업로드 완료 후 UI 업데이트
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(UploadImage.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.ic_add_photo);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    // 업로드 진행 중일 때 프로그레스 바 표시
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 업로드 실패 시 에러 메시지 표시
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(UploadImage.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    // 파일의 확장자 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // 이미지 크기 조정
    private Bitmap resizeImage(Bitmap originalImage, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, false);
    }

    // 이미지 크기 조정 및 테두리 추가
    private Bitmap adjustImageSizeAndAddBorder(Bitmap originalBitmap, int desiredWidth, int desiredHeight) {
        // 원본 이미지의 너비와 높이를 가지고옴
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 새로운 크기의 비트맵을 생성
        Bitmap adjustedBitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, originalBitmap.getConfig());
        // 캔버스 객체를 생성하여 비트맵에 그리기 작업 수행
        Canvas canvas = new Canvas(adjustedBitmap);

        // 캔버스의 배경색을 검정색으로 설정합니다.
        canvas.drawColor(Color.BLACK);

        // 원본 이미지를 조정된 크기의 비트맵에 그린다
        // 이미지를 가운데로 정렬하기 위해 x 좌표를 계산
        int x = (desiredWidth - width) / 2;
        // 이미지를 상단에 정렬하기 위해 y 좌표를 0으로 설정
        int y = 0;
        canvas.drawBitmap(originalBitmap, x, y, null);

        // 흰색 픽셀을 검정색으로 변경하여 테두리를 추가
        for (int row = 0; row < desiredWidth; row++) {
            for (int col = 0; col < desiredHeight; col++) {
                int pixel = adjustedBitmap.getPixel(row, col);
                // 픽셀이 흰색인 경우 검정색으로 변경
                if (pixel == Color.WHITE) {
                    adjustedBitmap.setPixel(row, col, Color.BLACK);
                }
            }
        }
        // 조정된 비트맵을 반환합니다.
        return adjustedBitmap;
    }

}
