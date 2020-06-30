package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.library.Model.Book;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Book Activity class which presents information about book and enables editing them.
 */
public class Book_Activity extends AppCompatActivity {

    private TextView tvtitle, tvauthor, tvdescription;
    private Button btnconfirm, btndelete;
    private ImageView img;
    private boolean flag = true;

    FirebaseDatabase database;
    DatabaseReference DBreference;

    private String id;
    private String photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_);

        tvtitle = findViewById(R.id.txttitle);
        tvauthor = findViewById(R.id.txtauthor);
        tvdescription = findViewById(R.id.txtdescription);
        btnconfirm = findViewById(R.id.btnConfirm);
        btndelete = findViewById(R.id.btnDelete);
        img = findViewById(R.id.photo);

        Intent intent = getIntent();
        final String Title = intent.getExtras().getString("Title");
        final String Author = intent.getExtras().getString("Author");
        final String Description = intent.getExtras().getString("Description");
        photo = intent.getExtras().getString("Photo");
        final String Id = intent.getExtras().getString("Id");

        getSupportActionBar().setTitle(Title);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        tvtitle.setText(Title);
        tvauthor.setText(Author);
        tvdescription.setText(Description);

        id=Id;

        img.setImageResource(R.drawable.plus);

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference("Library");

        try{
            Picasso.get()
                    .load(photo)
                    .fit()
                    .centerCrop()
                    .into(img);
        }catch(Exception e){
            img.setImageResource(R.drawable.plus);
        }

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase(Id);
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook(Id);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Book_Activity.this, Image_Activity.class);

                intent.putExtra("Title", Title);
                intent.putExtra("Author", Author);
                intent.putExtra("Description", Description);
                intent.putExtra("Id", Id);

                flag=false;

                startActivity(intent);
            }
        });
    }

    /**
     * Gets information from edit text and updates book in database.
     * @param key
     *
     */
    private void saveToFirebase(final String key) {

        String title = tvtitle.getText().toString();
        String author = tvauthor.getText().toString();
        String description = tvdescription.getText().toString();

        final Book book = new Book(title, author, description);
        book.setId(key);

        DBreference.child(key).orderByChild("photo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String ds = dataSnapshot.getValue(String.class);
                if (dataSnapshot.getKey().equals("photo")) {
                    book.setPhoto(ds);
                    book.setId(key);
                    DBreference.child(key).setValue(book);
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

        Toast.makeText(Book_Activity.this, "Book is updated", Toast.LENGTH_SHORT).show();

        finish();
    }


    /**
     * Deletes book from database.
     * @param key
     */
    private void deleteBook(String key) {

        DBreference.child(key).removeValue();
        this.finish();
    }


    /**
     * Updates displayed information about book.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (flag == false) {

            DBreference.child(id).orderByChild("photo").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    String ds = dataSnapshot.getValue(String.class);
                    if (dataSnapshot.getKey().equals("photo")) {
                        try {
                            Picasso.get()
                                    .load(ds)
                                    .fit()
                                    .centerCrop()
                                    .into(img);
                        } catch (Exception e) {
                            img.setImageResource(R.drawable.plus);
                        }
                    }
                    if(dataSnapshot.getKey().equals("title")){
                        tvtitle.setText(ds);
                    }
                    if(dataSnapshot.getKey().equals("author")){
                        tvauthor.setText(ds);
                    }
                    if(dataSnapshot.getKey().equals("description")){
                        tvdescription.setText(ds);
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







