 package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.library.Model.Book;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


 /**
  * Main Activity class which supports methods of library catalog view, search view and adding button. Also connects with Firebase and retrieves data.
  */
 public class MainActivity extends AppCompatActivity {


     private RecyclerView recyclerView;
     private FloatingActionButton fab;
     private EditText editText;
     ArrayList<Book> arrayList;

    FirebaseDatabase database;
    DatabaseReference DBreference;

    FirebaseRecyclerOptions<Book> options;
    FirebaseRecyclerAdapter<Book, RecyclerViewAdapter.MyViewHolder> adapter;

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.floating_action_button);
        editText = findViewById(R.id.editText);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                {
                    search(s.toString());
                }
                else
                    {
                    search(" ");
                    }


            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                Intent intent = new Intent(MainActivity.this, InputActivity.class );
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        DBreference = database.getReference("Library");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager
                (this, 2, GridLayoutManager.VERTICAL, false));

        showBooks();
    }

     /**
      *Sets FirebaseRecyclerAdapter.
      */
     private void showBooks() {

        options = new FirebaseRecyclerOptions.Builder<Book>()
                .setQuery(DBreference, Book.class)
                .build();



         adapter = new FirebaseRecyclerAdapter<Book, RecyclerViewAdapter.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder bookViewHolder, final int i, @NonNull final Book book) {

                bookViewHolder.text_title.setText(book.getTitle());
                bookViewHolder.text_author.setText(book.getAuthor());
                bookViewHolder.imgView.setImageResource(R.drawable.plus);

                try{
                    Picasso.get()
                            .load(book.getPhoto())
                            .fit()
                            .centerCrop()
                            .into(bookViewHolder.imgView);
                }catch(Exception e){
                    bookViewHolder.imgView.setImageResource(R.drawable.plus);
                }

                bookViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        arrayList.clear();

                        Intent intent = new Intent(MainActivity.this ,Book_Activity.class);

                        intent.putExtra("Title",book.getTitle());
                        intent.putExtra("Author", book.getAuthor());
                        intent.putExtra("Description", book.getDescription());
                        intent.putExtra("Photo", book.getPhoto());
                        intent.putExtra("Id", book.getId());

                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row, parent, false);
                return new RecyclerViewAdapter.MyViewHolder(itemView);
            }
        };
        recyclerView.setAdapter(adapter);
    }

     /**
      * Searches book's titles which starts with string which user wrote in search view and sets view adapter with results.
      * @param s
      */
    private void search(String s){

        Query query = DBreference.orderByChild("title")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    arrayList.clear();
                    for (DataSnapshot dssL : dataSnapshot.getChildren()) {
                        final Book book = dssL.getValue(Book.class);
                        arrayList.add(book);
                    }

                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), arrayList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.setItems(arrayList);
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    arrayList.clear();
                    setList();
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), arrayList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.setItems(arrayList);
                    recyclerViewAdapter.notifyDataSetChanged();

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


     /**
      * Sets book's list to display from database;
      */
    public void setList(){
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dssL: dataSnapshot.getChildren())
                {
                    final Book book = dssL.getValue(Book.class);
                    arrayList.add(book);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

     @Override
     protected void onStart() {
         super.onStart();
         adapter.startListening();
     }

     @Override
     protected void onStop() {
         super.onStop();
         adapter.stopListening();
     }

     /**
      * Runs update or delete menu option, according to user choice.
      * @param item
      * @return
      */
     @Override
     public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Update")) {
            Toast.makeText(MainActivity.this, adapter.getRef(item.getOrder()).getKey(), Toast.LENGTH_LONG).show();

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if(item.getTitle().equals("Delete")){
            deleteTask(adapter.getRef(item.getOrder()).getKey());
        }
         return super.onContextItemSelected(item);
     }

     /**
      * Deletes book from database.
      * @param key
      */
     private void deleteTask(String key) {
         DBreference.child(key).removeValue();
     }


     /**
      * Shows update dialog which enables to update books quickly.
      * @param key
      * @param item
      */
     private void showUpdateDialog(final String key, Book item) {

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Update");
         builder.setMessage("Please update the fields");

         View update_layout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);

         final EditText edit_update_title = update_layout.findViewById(R.id.edit_update_title);
         final EditText edit_update_author = update_layout.findViewById(R.id.edit_update_author);
         final EditText edit_update_description = update_layout.findViewById(R.id.edit_update_description);

         edit_update_title.setText(item.getTitle());
         edit_update_author.setText(item.getAuthor());
         edit_update_description.setText(item.getDescription());

         builder.setView(update_layout);
         builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

             @Override
             public void onClick(DialogInterface dialog, int which) {

                 String title = edit_update_title.getText().toString();
                 String author = edit_update_author.getText().toString();
                 String description = edit_update_description.getText().toString();
                 Book toDo = new Book(title, author, description);
                 DBreference.child(key).setValue(toDo);

                 Toast.makeText(MainActivity.this, "Task is updated", Toast.LENGTH_SHORT).show();

             }
         });

         builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

                 dialog.dismiss();
             }
         });
         builder.show();
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
         return super.onCreateOptionsMenu(menu);
     }

     /**
      * Supports deleting all books option.
      * @param item
      * @return
      */
     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.delete_all){
            DBreference.removeValue();
        }
         return super.onOptionsItemSelected(item);
     }


 }
