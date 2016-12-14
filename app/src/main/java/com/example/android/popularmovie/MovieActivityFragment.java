package com.example.android.popularmovie;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovie.Adapter.MovieAdapter;
import com.example.android.popularmovie.Models.Movie;
import com.example.android.popularmovie.DataBase.MoviesContract;


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


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieActivityFragment extends Fragment {

    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;

    public interface Callback {
        void onItemSelected(Movie movie);
    }

    public MovieActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            movies = new ArrayList<>();
        }
        else {
            movies = savedInstanceState.getParcelableArrayList("movies");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            new FetchMovieData().execute("popular");
            return true;
        }
        if(id == R.id.action_top_rated){
            new FetchMovieData().execute("top_rated");
            return true;
        }
        if(id == R.id.action_favorite){

            final ContentResolver resolver = getActivity().getContentResolver();
            final Cursor cursor =
                    resolver.query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);

            final int COLUMN_ID = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
            final int COLUMN_OVERVIEW =
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
            final int COLUMN_POSTER_URL =
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_URL);
            final int COLUMN_RELEASE_DATE =
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
            final int COLUMN_TITLE = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
            final int COLUMN_VOTE_AVERAGE =
                    cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);

            if (cursor.moveToFirst()) {
                final Movie[] movies = new Movie[cursor.getCount()];
                int count = 0;
                while (!cursor.isAfterLast()) {

                    String pics = cursor.getString(COLUMN_POSTER_URL);
                    String a[] = pics.split(",");

                    final Movie movie = new Movie(a[0],
                            cursor.getString(COLUMN_OVERVIEW),
                            cursor.getString(COLUMN_RELEASE_DATE),
                            cursor.getString(COLUMN_ID),
                            cursor.getString(COLUMN_TITLE),
                            a[1],
                            cursor.getString(COLUMN_VOTE_AVERAGE));


                    movies[count] = movie;
                    count++;
                    cursor.moveToNext();
                }
                cursor.close();

                movieAdapter.clear();
                for (final Movie movie : movies) {
                    movieAdapter.add(movie);
                    Log.v("MovieAdapter", movie.id);
                }
                movieAdapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie, container, false);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) root.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = movieAdapter.getItem(i);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchMovieData().execute("popular");
    }

    private void updateData(String type){
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(type);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        super.onSaveInstanceState(outState);
    }

    public class FetchMovieData extends AsyncTask<String, Void, ArrayList<Movie>>{

        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException{
            ArrayList<Movie> moviesData = new ArrayList<Movie>();

            final String RESULTS = "results";
            final String POSTER = "poster_path";
            final String OVERVIEW = "overview";
            final String REL_DATE = "release_date";
            final String TITLE = "title";
            final String VOTE_AVG = "vote_average";
            final String BACKDROP_IMG = "backdrop_path";
            final String ID = "id";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            for(int i = 0; i < movieArray.length(); i++){
                String movieTitle = movieArray.getJSONObject(i).getString(TITLE);
                String moviePoster = "http://image.tmdb.org/t/p/w500/" +
                        movieArray.getJSONObject(i).getString(POSTER);
                String movieOverview = movieArray.getJSONObject(i).getString(OVERVIEW);
                String movieRelDate = movieArray.getJSONObject(i).getString(REL_DATE);
                String movieVoteAvg = movieArray.getJSONObject(i).getString(VOTE_AVG);
                String backdropImg = "http://image.tmdb.org/t/p/w500/" +
                        movieArray.getJSONObject(i).getString(BACKDROP_IMG);
                String id = movieArray.getJSONObject(i).getString(ID);
                Movie movie = new Movie(moviePoster, movieOverview, movieRelDate, id, movieTitle, backdropImg, movieVoteAvg);

                moviesData.add(i, movie);
            }

            return moviesData;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?api_key=";
                final String API_KEY = getResources().getString(R.string.api_key);
                Uri builtUri = Uri.parse(MOVIE_BASE_URL + API_KEY).buildUpon().build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }


                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Error ", String.valueOf(e));

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Fetch", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            if(movieArrayList != null){
                movieAdapter.clear();
                for(Movie movie: movieArrayList){

                    movieAdapter.add(movie);
                }
            }
            movies.addAll(movieArrayList);
        }
    }
}
