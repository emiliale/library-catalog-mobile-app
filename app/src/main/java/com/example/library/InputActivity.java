package com.example.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.library.Model.Book;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Activity which enable adding book to catalog.
 */
public class InputActivity extends AppCompatActivity {

    private EditText editTitle, editAuthor, editDescription;
    private Button btnAdd;
    private ImageView btnPhoto;

    FirebaseDatabase database;
    DatabaseReference DBreference;

    private boolean flag = true;
    private String id;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        editTitle = findViewById(R.id.edit_tile);
        editAuthor = findViewById(R.id.edit_author);
        editDescription = findViewById(R.id.edit_description);
        btnPhoto = findViewById(R.id.photo_btn);
        btnAdd = findViewById(R.id.btn_add);

        btnPhoto.setImageResource(R.drawable.plus);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference("Library");

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InputActivity.this, Image_Activity.class);

                intent.putExtra("Title", editTitle.getText());
                intent.putExtra("Author", editAuthor.getText());
                intent.putExtra("Description", editDescription.getText());
                intent.putExtra("Id", id);
                flag = false;

                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase();
            }
        });

        initializeBook();

    }


    /**
     * Creates default book with empty fields.
     */
    private void initializeBook() {

        book = new Book("", "", "");

        DBreference.push().setValue(book, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                String uniqueKey = databaseReference.getKey();
                book.setId(uniqueKey);
                id = uniqueKey;
                DBreference.child(uniqueKey).setValue(book);
            }
        });
    }

    /**
     * Gets information from edit texts and saves book to database.
     */
    private void saveToFirebase() {

        String title = editTitle.getText().toString();
        String author = editAuthor.getText().toString();
        String description = editDescription.getText().toString();

        book = new Book(title, author, description);

        DBreference.child(id).orderByChild("photo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String bookInfo = dataSnapshot.getValue(String.class);
                if (dataSnapshot.getKey().equals("photo")) {
                    book.setPhoto(bookInfo);
                    book.setId(id);
                    DBreference.child(id).setValue(book);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        flag=false;
        finish();
    }

    /**
     * If user didn't confirm adding books, deletes book from database.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (flag) {
            DBreference.child(id).removeValue();
        }
    }

    /**
     *  Updates displayed information about book.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (flag == false) {

            DBreference.child(id).orderByChild("photo").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    String dinosaur = dataSnapshot.getValue(String.class);
                    if (dataSnapshot.getKey().equals("photo")) {
                        try {
                            Picasso.get()
                                    .load(dinosaur)
                                    .fit()
                                    .centerCrop()
                                    .into(btnPhoto);
                        } catch (Exception e) {
                            btnPhoto.setImageResource(R.drawable.plus);
                        }
                    }
                    if(dataSnapshot.getKey().equals("title")){
                        editTitle.setText(dinosaur);
                    }
                    if(dataSnapshot.getKey().equals("author")){
                        editAuthor.setText(dinosaur);
                    }
                    if(dataSnapshot.getKey().equals("description")){
                        editDescription.setText(dinosaur);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        flag = true;
    }
}
