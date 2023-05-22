package com.example.helpmeplz;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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


    private ImageView imageView;
    private ProgressBar progressBar;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    private EditText upToTime;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        Button upload_Back = findViewById(R.id.upload_back);

        Button uploadBtn = findViewById(R.id.upload_btn);
        progressBar = findViewById(R.id.progress_View);
        imageView = findViewById(R.id.image_view);

        progressBar.setVisibility(View.INVISIBLE);

        upToTime = findViewById(R.id.upload_upToTime);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        upload_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadImage.this, Menu.class);
                startActivity(intent);

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadBtn.setEnabled(false);

                if (imageUri != null) {

                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadImage.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/");
                    activityResult.launch(galleryIntent);

                }
            }
        });
    }

    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        imageUri = result.getData().getData();

                        imageView.setImageURI(imageUri);
                    }
                }
            });


    private void uploadToFirebase(Uri uri) {

        try {
            Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            int desiredWidth = 0; // 원하는 너비
            int desiredHeight = 0; // 원하는 높이

            // upToTime 값에 따라 사진 크기 조정
            int upToTimeValue = Integer.parseInt(upToTime.getText().toString());
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
                Toast.makeText(UploadImage.this, "유효하지 않는 숫자입니다", Toast.LENGTH_SHORT).show();
            }

            Bitmap resizedImage = resizeImage(originalImage, desiredWidth, desiredHeight);

            Bitmap adjustedBitmap = adjustImageSizeAndAddBorder(resizedImage, 200, 517);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            adjustedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            UploadTask uploadTask = fileRef.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrl = uri.toString();

                            Model model = new Model(imageUrl);
                            String modelId = root.push().getKey();
//                            root.child(modelId).setValue(model);
//
                            String userId = mAuth.getCurrentUser().getUid();

                            mDatabase.child("users").child(userId).child("image").setValue(imageUrl);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(UploadImage.this, "업로드 성공", Toast.LENGTH_SHORT).show();

                            imageView.setImageResource(R.drawable.ic_add_photo);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(UploadImage.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private Bitmap resizeImage(Bitmap originalImage, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, false);
    }


    private Bitmap adjustImageSizeAndAddBorder(Bitmap originalBitmap, int desiredWidth, int desiredHeight) {

        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();


        Bitmap adjustedBitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, originalBitmap.getConfig());


        Canvas canvas = new Canvas(adjustedBitmap);


        canvas.drawColor(Color.BLACK);


        canvas.drawBitmap(originalBitmap, (desiredWidth - width) / 2, 0, null);

        return adjustedBitmap;
    }


}



