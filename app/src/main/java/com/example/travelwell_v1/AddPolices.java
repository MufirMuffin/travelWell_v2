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

public class AddPolices extends AppCompatActivity {

    EditText addpolice_name, addpolice_description,addpolice_number, addpolice_latitude, addpolice_longitude;
    ImageView addpolice_image;
    Button addnewpolice_button;
    FirebaseStorage storage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_polices);

        addpolice_name = findViewById(R.id.addpolice_name);
        addpolice_description = findViewById(R.id.addpolice_description);
        addpolice_number = findViewById(R.id.addpolice_number);
        addpolice_latitude = findViewById(R.id.addpolice_latitude);
        addpolice_longitude = findViewById(R.id.addpolice_longitude);
        addpolice_image = findViewById(R.id.addpolice_image);
        addnewpolice_button = findViewById(R.id.addnewpolice_button);
        storage = FirebaseStorage.getInstance();

        addpolice_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        addnewpolice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addpolice_name.getText().toString();
                String description = addpolice_description.getText().toString();
                String number = addpolice_number.getText().toString();
                double latitude = Double.parseDouble(addpolice_latitude.getText().toString());
                double longitude = Double.parseDouble(addpolice_longitude.getText().toString());

                // Create a new instance of the Place class with the entered values
                Polices newPolice = new Polices(name, description, number, "", latitude, longitude);

                // Get a reference to the "places" node in the database
                DatabaseReference policesRef = FirebaseDatabase.getInstance().getReference().child("polices");

                // Push the new place object to the database
                DatabaseReference newPoliceRef = policesRef.push();
                String policeKey = newPoliceRef.getKey();
                newPoliceRef.setValue(newPolice);

                // Upload the image if it exists
                if (imageUri != null) {
                    uploadImage(policeKey);
                } else {
                    Toast.makeText(AddPolices.this, "Successfully Added New Police Station", Toast.LENGTH_SHORT).show();
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
            addpolice_image.setImageURI(imageUri);
        }
    }

    private void uploadImage(String policeKey){
        StorageReference storageReference = storage.getReference();
        String filename = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("policeImages/"+filename);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String image = uri.toString();
                        DatabaseReference policesRef = FirebaseDatabase.getInstance().getReference().child("polices");
                        policesRef.child(policeKey).child("image").setValue(image);

                        Toast.makeText(AddPolices.this, "Successfully Added New Police Station", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPolices.this, "Failed to Add New Police Station", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPolices.this, "Image upload failed", Toast.LENGTH_SHORT).show();

            }
        });
    }





}