package com.example.travelwell_v1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddHospitals extends AppCompatActivity {

    EditText addhospital_name, addhospital_description, addhospital_number, addhospital_latitude, addhospital_longitude;
    ImageView addhospital_image;
    Button addnewhospital_button;
    FirebaseStorage storage;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hospitals);

        addhospital_name = findViewById(R.id.addhospital_name);
        addhospital_description = findViewById(R.id.addhospital_description);
        addhospital_number = findViewById(R.id.addhospital_number);
        addhospital_latitude = findViewById(R.id.addhospital_latitude);
        addhospital_longitude = findViewById(R.id.addhospital_longitude);
        addhospital_image = findViewById(R.id.addhospital_image);
        addnewhospital_button = findViewById(R.id.addnewhospital_button);
        storage = FirebaseStorage.getInstance();

        addhospital_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        addnewhospital_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addhospital_name.getText().toString();
                String description = addhospital_description.getText().toString();
                String number = addhospital_number.getText().toString();
                double latitude = Double.parseDouble(addhospital_latitude.getText().toString());
                double longitude = Double.parseDouble(addhospital_longitude.getText().toString());

                Hospitals newHospital = new Hospitals(name,description,number,"",latitude,longitude);

                DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference().child("hospitals");

                DatabaseReference newHospitalRef = hospitalRef.push();
                String hospitalKey = newHospitalRef.getKey();
                newHospitalRef.setValue(newHospital);

                // Upload the image if it exists
                if (imageUri != null) {
                    uploadImage(hospitalKey);
                } else {
                    Toast.makeText(AddHospitals.this, "Successfully Added New Hospital", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addhospital_image.setImageURI(imageUri);
        }
    }

    private void uploadImage(String hospitalKey){
        StorageReference storageReference = storage.getReference();
        String filename = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("hospitalImage/"+filename);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String image = uri.toString();
                        DatabaseReference hospitalRef = FirebaseDatabase.getInstance().getReference().child("hospitals");
                        hospitalRef.child(hospitalKey).child("image").setValue(image);

                        Toast.makeText(AddHospitals.this, "Successfully Added New Hospital", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddHospitals.this, "Failed to Add New Hospital", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddHospitals.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}