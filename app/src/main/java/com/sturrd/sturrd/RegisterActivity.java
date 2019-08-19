package com.sturrd.sturrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText  mName;
    private Button mRegistration;
    private String currentUId, userId,
            name,
            profileImageUrl,
            userSex,
            age;
    private DatabaseReference usersDb;
    private RadioRealButtonGroup mRadioGroup;

    private FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_details);
        mProfileImage = findViewById(R.id.profile_image);
        client = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });



        mName = findViewById(R.id.name);

        mRegistration = findViewById(R.id.register);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mRadioGroup.setPosition(0);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString();
                final String accountType;
                int selectId = mRadioGroup.getPosition();


                switch (selectId) {
                    case 1:
                        accountType = "Female";
                        break;
                    default:
                        accountType = "Male";
                }

                if(resultUri != null) {
                    final DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filePath.putBytes(data);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                            return;
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map newImage = new HashMap();
                                    newImage.put("profileImageUrl", uri.toString());
                                    currentUserDb.updateChildren(newImage);

                                    finish();
                                    return;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    finish();
                                    return;
                                }
                            });
                        }
                    });
                }


                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                Map userInfo = new HashMap<>();
                userInfo.put("name", name);
                userInfo.put("sex", accountType);
                userInfo.put("profileImageUrl", "default");
                switch (accountType) {
                    case "Male":
                        userInfo.put("interest", "Female");
                        break;
                    case "Female":
                        userInfo.put("interest", "Male");
                        break;
                }
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

                currentUserDb.updateChildren(userInfo);
                locationUpdate();
                requestPermissions();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                Glide.with(getApplication())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void locationUpdate() {
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    String latitudeString = String.valueOf(latitude);
                    String longitudeString = String.valueOf(longitude);

                    //usersDb.child("latitude").child(latitudeString).setValue(true);
                    //usersDb.child("longitude").child(longitudeString).setValue(true);

                    Map userLatLng = new HashMap();
                    userLatLng.put("latitude", latitudeString);
                    userLatLng.put("longitude", longitudeString);

                    currentUId = mAuth.getCurrentUser().getUid();

                    usersDb.child(currentUId).child("LatLng").updateChildren(userLatLng);
                }

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

}
