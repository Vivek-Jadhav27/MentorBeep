package com.example.bookbreeze;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


public class FavouriteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

     private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    BookAdapter bookadapter;
    List<BookData> bookList = new ArrayList<>();
    private DatabaseReference userRef;
    private String currentUserId;
    String[] favoriteBookIds ={} ;
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView loadingAnimationView;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance(String param1, String param2) {
        FavouriteFragment fragment = new FavouriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView =view.findViewById(R.id.favrecycle);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loadingAnimationView = view.findViewById(R.id.favorAnimationView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        bookadapter = new BookAdapter(getContext(),bookList,false);
        recyclerView.setAdapter(bookadapter);
        recyclerView.hasFixedSize();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId);
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot favoritesSnapshot = dataSnapshot.child("favorites");

                // Initialize the array
                favoriteBookIds = new String[(int) favoritesSnapshot.getChildrenCount()];
                int index = 0;

                for (DataSnapshot bookSnapshot : favoritesSnapshot.getChildren()) {
                    String bookId = bookSnapshot.getKey();
                    favoriteBookIds[index] = bookId;
                    index++;

                    // For testing, you can remove this toast in the final version
                }
                new FetchBookTask().execute(favoriteBookIds);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadingAnimationView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        new FetchBookTask().execute(favoriteBookIds);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform the data refresh operation
                refreshData();
            }
        });

        return view;
    }
    private void refreshData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot favoritesSnapshot = dataSnapshot.child("favorites");
                favoriteBookIds = new String[(int) favoritesSnapshot.getChildrenCount()];
                int index = 0;

                for (DataSnapshot bookSnapshot : favoritesSnapshot.getChildren()) {
                    String bookId = bookSnapshot.getKey();
                    favoriteBookIds[index] = bookId;
                    index++;

                }
                new FetchBookTask().execute(favoriteBookIds);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        new FetchBookTask().execute(favoriteBookIds);
        swipeRefreshLayout.setRefreshing(false);
    }

    private class FetchBookTask extends AsyncTask<String, Void, List<BookData>> {

        @Override
        protected List<BookData> doInBackground(String... params) {
            List<BookData> books = new ArrayList<>();

            for (String bookId : params) {
                try {
                    String apiUrl = "https://www.googleapis.com/books/v1/volumes/" + bookId;
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

                        String result = stringBuilder.toString();
                        BookData book = parseBookData(result);
                        if (book != null) {
                            books.add(book);
                        }

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return books;
        }

        @Override
        protected void onPostExecute(List<BookData> books) {
            if (!books.isEmpty()) {
                // Update UI with the list of books
                bookadapter.updateData(books);

                loadingAnimationView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        private BookData parseBookData(String result) {
            try {
                JSONObject json = new JSONObject(result);
                JSONObject volumeInfo = json.getJSONObject("volumeInfo");

                String id = json.getString("id");
                String title = volumeInfo.getString("title");

                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                List<String> authors = new ArrayList<>();
                for (int j = 0; j < authorsArray.length(); j++) {
                    authors.add(authorsArray.getString(j));
                }
                String concatenatedAuthors = String.join(", ", authors);

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imgUrl = imageLinks.getString("thumbnail") + "&key=AIzaSyAKKNLnP52Zt24MRk6KFC5zm6d_YUuhtKA";
                imgUrl = imgUrl.replace("http://", "https://");

                BookData book = new BookData(title, concatenatedAuthors, imgUrl);
                book.setId(id);
                return book;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}