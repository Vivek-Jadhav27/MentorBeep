package com.example.bookapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BookAdapter adapter;
    List<BookItem> bookList = new ArrayList<>();
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=subject:economic&download=epub&orderBy=newest"; // Replace with the actual API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(adapter);
        new FetchBooksTask().execute(API_URL);
    }

    private class FetchBooksTask extends AsyncTask<String, Void, String> {

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
                    List<BookItem> books = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {

                        JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                        String title = volumeInfo.getString("title");

                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        List<String> authors = new ArrayList<>();
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors.add(authorsArray.getString(j));
                        }
                        String concatenatedAuthors = String.join(", ", authors);

                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String imgURL = imageLinks.getString("thumbnail");

                        BookItem book = new BookItem(title, concatenatedAuthors, imgURL+"&key=AIzaSyAKKNLnP52Zt24MRk6KFC5zm6d_YUuhtKA");
                        books.add(book);
                    }
                    adapter.updateData(books);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}