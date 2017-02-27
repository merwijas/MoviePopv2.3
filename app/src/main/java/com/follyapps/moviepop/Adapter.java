package com.follyapps.moviepop;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.R.attr.duration;


public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    private List<Movie> mMovieData;

    private final AdapterOnClickHandler mClickHandler;

    public interface AdapterOnClickHandler {
        void onClick(Movie currentMovie);
    }

    // Add an AdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
    public Adapter(AdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    // Implement OnClickListener in the AdapterViewHolder class
    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;


        public AdapterViewHolder(View view) {
            super(view);

            mMovieImageView = (ImageView) view.findViewById(R.id.poster_image);
            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked movie's data to mClickHandler via its onClick method
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.i("Adapter", "adapterPostition in onClick" + adapterPosition);
            Movie movieChoice = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movieChoice);
        }
    }

      // This gets called when each new ViewHolder is created.
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AdapterViewHolder(view);
    }

     // OnBindViewHolder is called by the RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(AdapterViewHolder movieAdapterViewHolder, int position) {
        Movie movieForThisView = mMovieData.get(position);
        String posterName = movieForThisView.getPoster();
        Context context = movieAdapterViewHolder.itemView.getContext();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w500/" + posterName)
                .resize(800,900)
                .placeholder(R.drawable.popcorn)
                .into(movieAdapterViewHolder.mMovieImageView);
    }

    //This method simply returns the number of items to display.
    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    // this one updates the recyclerView by a framework method (I think)
    public void setMovieData(List<Movie> MovieData) {
        mMovieData = MovieData;
        notifyDataSetChanged();
    }

}
