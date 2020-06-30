package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.library.Model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 *Image Activity which supports choosing and uploading photo.
 */
public class Image_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnchooseImage;
    private Button btnupload;
    private ImageView imgv;
    private Uri imgUri;

    FirebaseDatabase database;
    private StorageReference storageRef;
    private DatabaseReference DBreference;
    private StorageTask uploadTask;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_);

        Intent intent = getIntent();
        final String Title = intent.getExtras().getString("Title");
        final String Author = intent.getExtras().getString("Author");
        final String Description = intent.getExtras().getString("Description");
        final String Id = intent.getExtras().getString("Id");

        book = new Book(Title, Author, Description);
        book.setId(Id);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        database = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("images");
        DBreference = FirebaseDatabase.getInstance().getReference("Library");

        btnchooseImage = findViewById(R.id.button_choose_image);
        btnupload = findViewById(R.id.button_upload);
        imgv = findViewById(R.id.image_view);

        btnchooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(Image_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile(Id);
                }
            }
        });
    }

    /**
     * Opens file chooser.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Loads photo to image view.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();

            Picasso.get().load(imgUri).into(imgv);
        }
    }

    /**
     * Gets file extension from uri.
     * @param uri
     * @return
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Uploads image to Firebase Storage
     * @param Id
     */
    private void uploadFile(final String Id) {
        if (imgUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imgUri));

            uploadTask = fileReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot
                                    .getStorage()
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            book.setPhoto(uri.toString());
                                            DBreference.child(Id).setValue(book);
                                            Toast.makeText(Image_Activity.this, "Upload successful", Toast.LENGTH_LONG).show();

                                            finish();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Image_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
