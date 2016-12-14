package com.example.android.popularmovie.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nihal on 12/11/16.
 */

public class Movie implements Parcelable {
    public String posterPath;
    public String overview;
    public String releaseDate;
    public String id;
    public String originatTtile;
    public String backdropPath;
    public String voteAverage;

    public Movie(String posterPath, String overview, String releaseDate, String id, String originatTtile, String backdropPath, String voteAverage) {
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.id = id;
        this.originatTtile = originatTtile;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
    }
    private Movie(Parcel in){
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.id = in.readString();
        this.originatTtile = in.readString();
        this.backdropPath = in.readString();
        this.voteAverage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.posterPath);
        parcel.writeString(this.overview);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.id);
        parcel.writeString(this.originatTtile);
        parcel.writeString(this.backdropPath);
        parcel.writeString(this.voteAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };




}
