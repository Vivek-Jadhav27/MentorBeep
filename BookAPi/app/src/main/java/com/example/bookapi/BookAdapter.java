    package com.example.bookapi;

    import android.content.Context;
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

    public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{

        private List<BookItem> bookList;
        private Context context;

        public BookAdapter(Context context, List<BookItem> bookList) {
            this.context = context;
            this.bookList = bookList;
        }

        public void updateData(List<BookItem> newBookList) {
            this.bookList = newBookList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
            BookItem book = bookList.get(position);

            holder.titleTextView.setText(book.title);
            holder.authorsTextView.setText(book.imageURL);

            Picasso.get()
                    .load(book.imageURL).placeholder(R.drawable.notice)
                    .into(holder.bookCoverImageView ,new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Log the error or take appropriate action
                            e.printStackTrace();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView bookCoverImageView;
            TextView titleTextView;
            TextView authorsTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bookCoverImageView = itemView.findViewById(R.id.bookimage);
                titleTextView = itemView.findViewById(R.id.booktitle);
                authorsTextView = itemView.findViewById(R.id.bookpublish);
            }
        }
    }
