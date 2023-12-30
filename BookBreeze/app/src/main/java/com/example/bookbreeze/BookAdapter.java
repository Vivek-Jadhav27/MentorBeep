package com.example.bookbreeze;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookData> bookList;
    private Context context;

    boolean flag;

    public BookAdapter(Context context, List<BookData> bookList, boolean flag) {
        this.context = context;
        this.bookList = bookList;
        this.flag = flag;
    }

    public void updateData(List<BookData> newBookList) {
        this.bookList = newBookList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (flag){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_horizon, parent, false);
            return new HorizonViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_vertical, parent, false);
            return new VerticalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BookData book = bookList.get(position);

        if (flag && holder instanceof HorizonViewHolder) {
            HorizonViewHolder horizonHolder = (HorizonViewHolder) holder;
            horizonHolder.titleTextView.setText(book.title);
            horizonHolder.authorsTextView.setText(book.author);

            Picasso.get()
                    .load(book.img)
                    .placeholder(R.drawable.notify)
                    .into(horizonHolder.bookCoverImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Log the error or take appropriate action
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    });

            horizonHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BookInfoActivity.class);
                    intent.putExtra("id",book.id);
                    context.startActivity(intent);
                    Toast.makeText(context, book.id+"Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (!flag && holder instanceof VerticalViewHolder) {
            VerticalViewHolder verticalHolder = (VerticalViewHolder) holder;
            verticalHolder.titleTextView.setText(book.title);
            verticalHolder.authorsTextView.setText(book.author);

            Picasso.get()
                    .load(book.img)
                    .placeholder(R.drawable.notify)
                    .into(verticalHolder.bookCoverImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Log the error or take appropriate action
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    });
            verticalHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BookInfoActivity.class);
                    intent.putExtra("id",book.id);
                    context.startActivity(intent);
                    Toast.makeText(context, book.id+"Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class HorizonViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView titleTextView;
        TextView authorsTextView;

        public HorizonViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.horizonImg);
            titleTextView = itemView.findViewById(R.id.horizonTitle);
            authorsTextView = itemView.findViewById(R.id.horizonAuthor);
        }
    }

    public static class VerticalViewHolder extends RecyclerView.ViewHolder {
        ImageView bookCoverImageView;
        TextView titleTextView;
        TextView authorsTextView;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            bookCoverImageView = itemView.findViewById(R.id.verticalImg);
            titleTextView = itemView.findViewById(R.id.verticalTitle);
            authorsTextView = itemView.findViewById(R.id.verticalAuthor);
        }
    }
}
