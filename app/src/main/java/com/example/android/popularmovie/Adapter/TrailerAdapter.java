package com.example.android.popularmovie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.android.popularmovie.Models.Trailer;
import com.example.android.popularmovie.R;

import java.util.List;

/**
 * Created by nihal on 12/11/16.
 */

public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailer_item,
                    parent, false);
        }

        Button button = (Button) convertView.findViewById(R.id.button_trailer);
        button.setText(trailer.name);


        return convertView;
    }
}
