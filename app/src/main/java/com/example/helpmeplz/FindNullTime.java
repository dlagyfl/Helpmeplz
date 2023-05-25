package com.example.helpmeplz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindNullTime extends AppCompatActivity {

    private TextView tt;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;

    private List<Mat> matList;
    private List<String> imageNames;
    private List<Bitmap> imageList;
    private StorageReference storageReference;
    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private int count = 0;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_null_time);

        tt = findViewById(R.id.tt);
//        imageView1 = findViewById(R.id.image1);
//        imageView2 = findViewById(R.id.image2);
//        imageView3 = findViewById(R.id.image3);
//        imageView4 = findViewById(R.id.image4);
//        imageView5 = findViewById(R.id.image5);

        imageNames = new ArrayList<>();
        imageList = new ArrayList<>();
        matList = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        String[] userUIDs = {
                "38D30CEbX4ew1Hj3QcIuG0Ea6WH2",
                "nleiHW8EWRahsDlhMsnGSvVEAqw2",
                "Ek8puwk8i4PfHoXoADDWN6GgMl22",
                "bOsTOkUzhETpH6NePX7K5IGqTxK2",
                "w2JSJTpFttTZ4aFpGTqGivRE9AI2"
        };

        for (String uid : userUIDs) {
            DatabaseReference timetableRef = databaseRef.child("users").child(uid).child("timetable");
            timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageName = dataSnapshot.getValue(String.class);
                    if (imageName != null) {
                        imageNames.add(imageName);
                    }
                    count++;

                    if (count == userUIDs.length) {
//                        downloadImagesAndProcess();
                        displayImages();
                        tt.setText(String.valueOf(matList.size()));
//                        tt.setText(String.valueOf(matList.size()));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void displayImages() {
//        TextView tt = findViewById(R.id.tt);
//        final ImageView imageView1 = findViewById(R.id.image1);
//        final ImageView imageView2 = findViewById(R.id.image2);
//        final ImageView imageView3 = findViewById(R.id.image3);
//        final ImageView imageView4 = findViewById(R.id.image4);
//        final ImageView imageView5 = findViewById(R.id.image5);

        for (int i = 0; i < imageNames.size(); i++) {
//            tt.setText("2");
            String imageUrl = imageNames.get(i);
            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
            final long MAX_IMAGE_SIZE = 1024 * 1024;

            final int finalI = i;
            imageRef.getBytes(MAX_IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                    Utils.bitmapToMat(bitmap, mat);
                    matList.add(mat);
//                    imageList.add(bitmap);
//                    switch (finalI) {
//                        case 0:
//                            imageView1.setImageBitmap(bitmap);
//                            break;
//                        case 1:
//                            imageView2.setImageBitmap(bitmap);
//                            break;
//                        case 2:
//                            imageView3.setImageBitmap(bitmap);
//                            break;
//                        case 3:
//                            imageView4.setImageBitmap(bitmap);
//                            break;
//                        case 4:
//                            imageView5.setImageBitmap(bitmap);
////                            tt.setText("10");
////                            tt.setText(imageList.size());
//                            break;
//                    }
//                    tt.setText(imageList.size());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }
    }
}







