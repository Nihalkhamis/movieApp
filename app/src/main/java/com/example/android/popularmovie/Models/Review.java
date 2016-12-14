package com.example.android.popularmovie.Models;

/**
 * Created by nihal on 12/11/16.
 */

public class Review {
    public String id;
    public String author;
    public String content;

    public Review(String id, String author, String content){
       this.id = id;
        this.author = author;
        this.content = content;
    }


}
