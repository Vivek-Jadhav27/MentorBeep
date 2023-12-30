package com.example.bookbreeze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookInfoActivity extends AppCompatActivity {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    String BookId , preview;
    ImageView bookImage;
    TextView  bookauthor , bookpublisher , bookpagecount , booktitle , booksubtitle, bookdescrip , booklang, bookcat;

    Button previewbtn , favorbtn;
    private DatabaseReference favoritesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_activty);

        Intent intent = getIntent();
        BookId = intent.getStringExtra("id");

        bookImage = findViewById(R.id.bookImage);
        booktitle = findViewById(R.id.booktitle);
        booksubtitle = findViewById(R.id.booksubtitle);
        bookdescrip = findViewById(R.id.bookdescrip);
        booklang = findViewById(R.id.booklanguage);
        bookcat = findViewById(R.id.bookcat);
        bookauthor = findViewById(R.id.bookauthor);
        bookpublisher = findViewById(R.id.bookpublish);
        bookpagecount = findViewById(R.id.bookpagecount);
        previewbtn = findViewById(R.id.btnpre);
        favorbtn = findViewById(R.id.btnfavor);

        new FetchBookTask().execute(API_URL+BookId);

        previewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                     Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(preview));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("favorites");
        }
        favoritesRef.child(BookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Book ID already exists, remove it from favorites
                    favorbtn.setText("Remove from Favorites");
                } else {
                    // Book ID doesn't exist, add it to favorites
                    favorbtn.setText("Add to Favorites");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        favorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String bookIdToAddOrRemove = "bookId123";

                // Check if the book ID already exists in the user's favorites
                favoritesRef.child(BookId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Book ID already exists, remove it from favorites
                            removeBookFromFavorites(BookId);
                        } else {
                            // Book ID doesn't exist, add it to favorites
                            addBookToFavorites(BookId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                    }
                });
            }
        });
    }
    private void addBookToFavorites(String bookId) {
        // Add the book ID to the favorites node of the current user
        favoritesRef.child(bookId).setValue(true);
        // Update button text or perform other UI changes (e.g., set text to "Remove from Favorites")
        favorbtn.setText("Remove from Favorites");
    }

    private void removeBookFromFavorites(String bookId) {
        // Remove the book ID from the favorites node of the current user
        favoritesRef.child(bookId).removeValue();
        // Update button text or perform other UI changes (e.g., set text to "Add to Favorites")
        favorbtn.setText("Add to Favorites");
    }
    private class FetchBookTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray items = json.getJSONArray("items");

                    // Assuming you want information for the first item in the array
                    JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

                    // Extracting book information
                    String title = volumeInfo.getString("title");
                    String subtitle = volumeInfo.optString("subtitle", "");
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    List<String> authors = new ArrayList<>();
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authors.add(authorsArray.getString(j));
                    }
                    String concatenatedAuthors = TextUtils.join(", ", authors);
                    String publisher = volumeInfo.optString("publisher", "");
                    int pageCount = volumeInfo.optInt("pageCount", 0);
                    String description = volumeInfo.optString("description", "");
                    String language = volumeInfo.optString("language", "");
                    preview = volumeInfo.optString("previewLink", "");
                    JSONArray categoriesArray = volumeInfo.optJSONArray("categories");
                    List<String> categories = new ArrayList<>();
                    if (categoriesArray != null) {
                        for (int k = 0; k < categoriesArray.length(); k++) {
                            categories.add(categoriesArray.getString(k));
                        }
                    }
                    String concatenatedCategories = TextUtils.join(", ", categories);

                    // Now you can use the extracted information as needed
                    // For example, you can display it in TextViews or log it
                    Log.d("BookInfo", "Title: " + title);
                    Log.d("BookInfo", "Subtitle: " + subtitle);
                    Log.d("BookInfo", "Authors: " + concatenatedAuthors);
                    Log.d("BookInfo", "Publisher: " + publisher);
                    Log.d("BookInfo", "Page Count: " + pageCount);
                    Log.d("BookInfo", "Description: " + description);
                    Log.d("BookInfo", "Language: " + language);
                    Log.d("BookInfo", "Categories: " + concatenatedCategories);

                    String imgUrl = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    imgUrl = imgUrl.replace("http://", "https://");

                    bookauthor.setText(String.format(":%s", concatenatedAuthors));
                    bookpublisher.setText(String.format(":%s",publisher));
                    bookpagecount.setText(String.format(": %d", pageCount));
                    booktitle.setText(title);
                    booksubtitle.setText(subtitle);
                    bookdescrip.setText(String.format(":%s",description));
                    booklang.setText(String.format(":%s",language));
                    bookcat.setText(String.format(":%s",concatenatedCategories));

                    Picasso.get()
                            .load(imgUrl)
                            .placeholder(R.drawable.notify)
                            .into(bookImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Image loaded successfully
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Log the error or take appropriate action
                                    e.printStackTrace();
                                    Toast.makeText(BookInfoActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                                }
                            });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }}
    }
}