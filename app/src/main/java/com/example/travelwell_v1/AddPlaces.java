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

public class AddPlaces extends AppCompatActivity {

    EditText addplace_name, addplace_description, addplace_latitude, addplace_longitude;
    ImageView addplace_image;
    FirebaseStorage storage;
    Button addnewplace_button;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);

        addplace_name = findViewById(R.id.addplace_name);
        addplace_description = findViewById(R.id.addplace_description);
        addplace_latitude = findViewById(R.id.addplace_latitude);
        addplace_longitude = findViewById(R.id.addplace_longitude);
        addplace_image = findViewById(R.id.addplace_image);
        addnewplace_button = findViewById(R.id.addnewplace_button);
        storage = FirebaseStorage.getInstance();

        addplace_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        addnewplace_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addplace_name.getText().toString();
                String description = addplace_description.getText().toString();
                double latitude = Double.parseDouble(addplace_latitude.getText().toString());
                double longitude = Double.parseDouble(addplace_longitude.getText().toString());

                // Create a new instance of the Place class with the entered values
                String placeKey = FirebaseDatabase.getInstance().getReference().child("places").push().getKey();
                Places newPlace = new Places(placeKey, name, description, "", latitude, longitude);

                // Get a reference to the "places" node in the database
                DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");

                // Push the new place object to the database
                placesRef.child(placeKey).setValue(newPlace);


                // Upload the image if it exists
                if (imageUri != null) {
                    uploadImage(placeKey);
                } else {
                    Toast.makeText(AddPlaces.this, "Successfully Added New Place", Toast.LENGTH_SHORT).show();
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
            addplace_image.setImageURI(imageUri);
        }
    }

    private void uploadImage(String placeKey) {
        StorageReference storageRef = storage.getReference();
        String filename = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child("images/" + filename);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();
                        DatabaseReference placesRef = FirebaseDatabase.getInstance().getReference().child("places");
                        placesRef.child(placeKey).child("imageURL").setValue(imageURL);

                        Toast.makeText(AddPlaces.this, "Successfully Added New Place", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPlaces.this, "Failed to get the download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPlaces.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
