package com.example.bookbreeze;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

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


public class HomeFragment extends Fragment {

     private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=java";

    private String mParam1;
    private String mParam2;
    private RecyclerView hrecyclerView;
    private RecyclerView vrecyclerView;
    private BookAdapter hadapter;
    private BookAdapter vadapter;
    List<BookData> bookList = new ArrayList<>();
    LottieAnimationView loadingAnimationView;
    LinearLayout layout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        loadingAnimationView = view.findViewById(R.id.homeAnimationView);
        layout = view.findViewById(R.id.linearlayout);

        hrecyclerView = view.findViewById(R.id.horizonrecyclerView);
        hrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        hadapter = new BookAdapter(getActivity(), bookList, true);
        hrecyclerView.setAdapter(hadapter);
        hrecyclerView.setHasFixedSize(true);

        vrecyclerView = view.findViewById(R.id.verticalrecyclerView);
        vrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        vadapter = new BookAdapter(getActivity(), bookList, false);
        vrecyclerView.setAdapter(vadapter);
        vrecyclerView.setHasFixedSize(true);

        loadingAnimationView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        new FetchBookTask().execute(API_URL);
        return view;
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
                    hadapter.updateData(books);
                    vadapter.updateData(books);

                    loadingAnimationView.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}