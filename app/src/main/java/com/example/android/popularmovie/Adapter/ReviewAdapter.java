package com.example.android.popularmovie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovie.Models.Review;
import com.example.android.popularmovie.R;

import java.util.List;

/**
 * Created by nihal on 12/11/16.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Review review = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_review_item,
                    parent, false);
        }

        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.review_author);
        reviewAuthor.setText(review.author);

        TextView reviewContent = (TextView) convertView.findViewById(R.id.review_content);
        reviewContent.setText(review.content);
        return convertView;
    }

}


