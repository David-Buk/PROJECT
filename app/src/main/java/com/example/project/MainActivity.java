package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri image;
    MaterialButton uploadImage, selectImage;
    ImageView imageView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    uploadImage.setEnabled(true);
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(imageView);
                }
            } else {
                Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressIndicator = findViewById(R.id.progress);

        imageView = findViewById(R.id.imageView);
        selectImage = findViewById(R.id.selectImage);
        uploadImage = findViewById(R.id.uploadImage);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(image);
            }
        });
    }

    private void uploadImage(Uri file) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Call your API endpoint with the download URL
                        String imageUrl = downloadUri.toString();
                        callYourApiEndpoint(imageUrl, ref);
                        Toast.makeText(MainActivity.this, "Got download URL", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
                progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
            }
        });
    }

    private void callYourApiEndpoint(String imageUrl, StorageReference ref) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"url\":\"" + imageUrl + "\"}");
        Request request = new Request.Builder()
                .url("https://nsfw-images-detection-and-classification.p.rapidapi.com/adult-content")
                .post(body)
                .addHeader("x-rapidapi-key", "b15b1b33a7msh13e4ac4017ae4a9p1f4deajsn842ee3d5c57a")
                .addHeader("x-rapidapi-host", "nsfw-images-detection-and-classification.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "API request failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API Response", responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        boolean isUnsafe = jsonObject.getBoolean("unsafe");

                        if (isUnsafe) {
                            callAgeDetectionApi(imageUrl, ref);
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Image is safe", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API Error", "Request failed with code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "API request unsuccessful", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void callAgeDetectionApi(String imageUrl, StorageReference ref) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"url\":\"" + imageUrl + "\"}");
        Request request = new Request.Builder()
                .url("https://age-detector.p.rapidapi.com/age-detection")
                .post(body)
                .addHeader("x-rapidapi-key", "b15b1b33a7msh13e4ac4017ae4a9p1f4deajsn842ee3d5c57a")
                .addHeader("x-rapidapi-host", "age-detector.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "API request failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("API Response", responseBody);

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        boolean deleteImage = false;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject faceObject = jsonArray.getJSONObject(i);
                            int age = faceObject.getInt("age");

                            if (age < 18) {
                                deleteImage = true;
                                break;
                            }
                        }

                        if (deleteImage) {
                            // Delete the image from Firebase Storage
                            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firebase", "Image deleted successfully");
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this, "Image deleted due to detection of underage person", Toast.LENGTH_SHORT).show();

                                        // Show the dialog to the user
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("Such images are not allowed!")
                                                .setPositiveButton("Learn More", (dialog, which) -> {
                                                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                                                    startActivity(intent);})
                                                .setNegativeButton("Cancel", null)
                                                .show();
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firebase", "Failed to delete image: " + e.getMessage());
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show());
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "This image may be prone to sextortion ", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API Error", "Request failed with code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "API request unsuccessful", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
