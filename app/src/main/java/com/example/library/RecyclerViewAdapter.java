package com.example.library;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.library.Model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * RecyclerViewAdapter provides a binding from an app-specific data set to views that are displayed within a RecyclerView.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context ;
    private ArrayList<Book> data ;


    public RecyclerViewAdapter(Context mContext, ArrayList<Book> mData) {
        this.context = mContext;
        this.data = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Book book = data.get(position);

        holder.text_title.setText(book.getTitle());
        holder.text_author.setText(book.getAuthor());

        holder.imgView.setImageResource(R.drawable.plus);

        try{
            Picasso.get()
                    .load(book.getPhoto())
                    .fit()
                    .centerCrop()
                    .into(holder.imgView);
        }catch(Exception e){
            holder.imgView.setImageResource(R.drawable.plus);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,Book_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                intent.putExtra("Title",book.getTitle());
                intent.putExtra("Author", book.getAuthor());
                intent.putExtra("Description", book.getDescription());
                intent.putExtra("Photo", book.getPhoto());
                intent.putExtra("Id", book.getId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(ArrayList<Book> books) {
        this.data = books;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView text_title, text_author;
        public ImageView imgView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text_title = itemView.findViewById(R.id.text_title);
            text_author = itemView.findViewById(R.id.text_author);
            imgView = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardview_id);

            itemView.setOnCreateContextMenuListener(this);
        }




        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select menu");
            menu.add(0,0, getAdapterPosition(), "Update");
            menu.add(0,1, getAdapterPosition(), "Delete");
        }
    }
}

