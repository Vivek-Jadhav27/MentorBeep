package com.example.bookbreeze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

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

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView textView;
    List<BookData> bookList = new ArrayList<>();
    BookAdapter bookadapter;
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=subject:";

    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        textView = findViewById(R.id.categoryName);
        recyclerView =findViewById(R.id.categoryRecycler);


        Intent intent = getIntent();
        category = intent.getStringExtra("category");

        textView.setText(category);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        bookadapter = new BookAdapter(this,bookList,false);
        recyclerView.setAdapter(bookadapter);
        recyclerView.hasFixedSize();


        new FetchBookTask().execute(API_URL+category+"&download=epub&orderBy=newest");
    }

    private class FetchBookTask extends AsyncTask<String , Void , String> {

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
                    List<BookData> books = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {

                        String id =items.getJSONObject(i).getString("id");
                        JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                        String title = volumeInfo.getString("title");

                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        List<String> authors = new ArrayList<>();
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors.add(authorsArray.getString(j));
                        }
                        String concatenatedAuthors = String.join(", ", authors);

                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String imgUrl = imageLinks.getString("thumbnail")+"&key=AIzaSyAKKNLnP52Zt24MRk6KFC5zm6d_YUuhtKA";
                        imgUrl = imgUrl.replace("http://", "https://");

                        BookData book = new BookData(title, concatenatedAuthors, imgUrl);
                        book.setId(id);
                        books.add(book);
                    }
                    bookadapter.updateData(books);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}