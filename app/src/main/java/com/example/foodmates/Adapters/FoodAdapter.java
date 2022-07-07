package com.example.foodmates.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmates.Activities.FoodDetailsActivity;
import com.example.foodmates.Models.Food;
import com.example.foodmates.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements Filterable {
    Context context;
    List<Food> foods;
    List<Food> feedsToShow;
    ImageView btnLike;
    ImageView btnLiked;
    ImageView btnSave;
    ImageView btnSaved;


    public FoodAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
        feedsToShow = new ArrayList<>(foods);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View foodView = LayoutInflater.from(context).inflate(R.layout.item_foods,parent,false);
        return new ViewHolder(foodView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food  = foods.get(position);
        holder.bind(food);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FoodDetailsActivity.class);
                intent.putExtra(Food.class.getSimpleName(), Parcels.wrap(food));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedsToShow.size();
    }

    @Override
    public Filter getFilter() {
        return fliterexample;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodTitle;
        ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodImage = itemView.findViewById(R.id.foodImage);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnLiked = itemView.findViewById(R.id.btnLiked);
            btnSaved = itemView.findViewById(R.id.btnSaved);
//            itemView.setOnClickListener(this);

        }

        public void bind(Food food) {
            foodTitle.setText(food.getTitle());
            String imageUrl;
            imageUrl = food.getImageurl();
            Glide.with(context).load((imageUrl)).into(foodImage);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FoodDetailsActivity.class);
                    intent.putExtra(Food.class.getSimpleName(), Parcels.wrap(food));
                    context.startActivity(intent);
                }
            });


            btnLike.setOnTouchListener(new View.OnTouchListener() {
                    GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener(){

                        @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Toast.makeText(context, "Double tap", Toast.LENGTH_SHORT).show();
                            btnLiked.setVisibility(View.VISIBLE);
                            btnLike.setVisibility(View.GONE);
                        return super.onDoubleTap(e);
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event );
                    return false;
                }
            });

            btnSave.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        btnSaved.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.GONE);
                        return super.onDoubleTap(e);
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });

        }

    }


    private Filter fliterexample = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Food> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                //If the user does not enter anything, display the whole list
                filteredList.addAll(foods);
            }
            else{
                //toLowercase makes it so that the search is not case sensitive
                //trim takes away extra whitespaces
                String filterPattern = constraint.toString().toLowerCase().trim();

                //iterate to see which post matched filterPattern
                for(Food food: foods){
                    if(food.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(food);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            feedsToShow.clear();
            if (results.values == null) {
                Log.e("PostsAdapter", "results.values is null");
            } else {
                feedsToShow.addAll((List) results.values);
                Log.d("PostsAdapter", "Displaying " + results.count + " results through filter");
            }
            notifyDataSetChanged();
        }
    };


}