package com.example.helpmeplz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        imageView1 = findViewById(R.id.image1);
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

        storageRef = storage.getReference();
//        storageReference = storageRef; // 추가

        String[] userUIDs = {
                "17rFTMDlLZdg6Xi35jCedwGkJJo1",
                "Cs1ME2O8oGO7iYbuqkZ2iBhYvCQ2",
                "gDdb8J1V40TdaUit6Axic5hWwmw2",
                "QPyoQ5NRcVTMZ5Iu9JdwMJKrOUj2",
                "IQSCh7QI4pUbxGfWKgFkuoAyiyp1"
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
//                        tt.setText(imageNames.get(1));
//                        downloadImagesAndProcess();
                        downloadImageUsingHttp();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void downloadImageUsingHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String imageUrl : imageNames) {
                    try {
                        URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream inputStream = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageList.add(bitmap);

                        inputStream.close();
                        connection.disconnect();
                    } catch (IOException e) {
                        Log.e("DownloadImage", "Error downloading image: " + e.getMessage());
                    }
                }

                // 이미지 다운로드가 완료되면 처리할 작업 수행
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processDownloadedImages();
                    }
                });
            }
        }).start();
    }


    private void processDownloadedImages() {
        // 이미지 처리 작업을 수행하거나 이미지를 화면에 표시하는 코드 작성

        // 이미지를 조합하여 하나의 이미지로 만듭니다.
        Bitmap combinationBitmap = combineImages(imageList);

        // OpenCV를 사용하여 이미지 처리 작업을 수행합니다.
        Mat combinationMat = new Mat();
        Utils.bitmapToMat(combinationBitmap, combinationMat);

        // 색상 범위에 따라 이미지를 이진화합니다.
        Mat dst = new Mat();
        Core.inRange(combinationMat, new Scalar(0, 0, 0), new Scalar(31, 31, 31), dst);

        // 이진화된 이미지를 원본 이미지에 적용합니다.
        Mat result = new Mat();
        Core.bitwise_and(combinationMat, combinationMat, result, dst);

        // 특정 색상으로 변경합니다.
        double[] color = { (byte)255, (byte)255, (byte)255 };  // 색상 설정 (여기서는 흰색)
        Mat maskedResult = new Mat();
        Core.copyTo(result, maskedResult, dst);
        maskedResult.setTo(new Scalar(color));

        // 처리된 이미지를 화면에 표시합니다.
        Bitmap processedBitmap = Bitmap.createBitmap(maskedResult.cols(), maskedResult.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(maskedResult, processedBitmap);
        tt.setText("1");
        imageView1.setImageBitmap(processedBitmap);
    }

    private Bitmap combineImages(List<Bitmap> images) {
        // 이미지를 조합하여 하나의 이미지로 만듭니다.
        int width = images.get(0).getWidth();
        int height = images.get(0).getHeight();
        Bitmap combinationBitmap = Bitmap.createBitmap(width * images.size(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinationBitmap);

        for (int i = 0; i < images.size(); i++) {
            Bitmap image = images.get(i);
            canvas.drawBitmap(image, width * i, 0, null);
        }

        return combinationBitmap;
    }



    //    private void downloadImagesAndProcess1() {
//            String imageName = imageNames.get(1);
////        for (String imageName : imageNames) {
//            StorageReference imageRef = storageRef.child(imageName);
//            final long MAX_IMAGE_SIZE = 1024 * 1024;
//
//            imageRef.getBytes(MAX_IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    imageView1.setImageBitmap(bitmap);
//
//                    // Resize the bitmap if needed
//                    // Example: Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
//
////                    Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
////                    Utils.bitmapToMat(bitmap, mat);
////                    matList.add(mat);
////
////                    if (matList.size() == imageNames.size()) {
////                        // Combine the images
////                        Mat resultMat = new Mat();
////                        Core.add(matList.get(0), matList.get(1), resultMat);
////                        for (int i = 2; i < matList.size(); i++) {
////                            Core.add(resultMat, matList.get(i), resultMat);
////                        }
////
////                        // Convert the result to Bitmap
////                        Bitmap resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888);
////                        Utils.matToBitmap(resultMat, resultBitmap);
////
////                        // Display the result in the ImageView
////                        imageView1.setImageBitmap(resultBitmap);
//                    }
//
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle failure
//                }
//            });
//        }
//    private void downloadImagesAndProcess() {
//        String imageUrl = imageNames.get(1); // 이미지 URL 가져오기
//
//        // Firebase Storage에서 이미지 다운로드
//        storageRef.child(imageUrl).getBytes(1000000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // 다운로드한 이미지 바이트 배열을 Bitmap으로 변환
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                // ImageView에 Bitmap 표시
//                imageView1.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // 이미지 다운로드 실패 시 처리할 내용
//            }
//        });
//    }
    private void downloadImagesAndProcess() {
        String imageUrl = imageNames.get(1); // 이미지 URL 가져오기

        // Firebase Storage에서 이미지 다운로드
        storageRef.child(imageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 이미지 다운로드 URL을 가져옴
                String downloadUrl = uri.toString();

                // 다운로드 URL을 사용하여 이미지에 접근 또는 처리
                // 예: 이미지 라이브러리로 이미지 표시, 이미지 처리 작업 등
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 다운로드 실패 시 처리할 내용
            }
        });
    }




}


