package com.example.foodmates.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodmates.Activities.FoodDetailsActivity;
import com.example.foodmates.Activities.PostDetailsActivity;
import com.example.foodmates.Models.Post;
import com.example.foodmates.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<Post> posts;


    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foods, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView foodImage;
        TextView foodTitle;
        Context context;
        TextView description;
        ImageView btnLike;
        ImageView btnLiked;
        ImageView btnSave;
        ImageView btnSaved;
        TextView createdAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.foodTitles);
            foodImage = itemView.findViewById(R.id.foodImages);
            username = itemView.findViewById(R.id.postUser);
            this.context = itemView.getContext();
//            description = itemView.findViewById(R.id.description)
            btnLike = itemView.findViewById(R.id.btnLike);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnLiked = itemView.findViewById(R.id.btnLiked);
            btnSaved = itemView.findViewById(R.id.btnSaved);
            createdAt = itemView.findViewById(R.id.createdAt);

        }
        public void bind(Post post) {
            foodTitle.setText(post.getKeyTitle());
            if (post.getUser()!= null){
            username.setText(post.getUser().getUsername());
            createdAt.setText(post.calculateTimeAgo());
            }
            if(post.getImageUrl() instanceof String){
            Glide.with(context).load((post.getImageUrl())).into(foodImage);
            }
            else {
                Glide.with(context).load(post.getImage().getUrl()).into(foodImage);
            }


            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Post> relation = user.getRelation("likes");
            ParseRelation<Post> savedRelation = user.getRelation("savedPost");
            ParseQuery<Post> postQuery = relation.getQuery();
            ParseQuery<Post> savedPostQuery = savedRelation.getQuery();


            postQuery.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> liked, ParseException e) {
                    for (Post p : liked){
                        if(Objects.equals(p.getObjectId(), post.getObjectId())){
                            btnLiked.setVisibility(View.VISIBLE);
                            btnLike.setVisibility(View.INVISIBLE);
                            return;
                        }
                        else {
                            btnLiked.setVisibility(View.INVISIBLE);
                            btnLike.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            savedPostQuery.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> saved, ParseException e) {
                    for (Post s : saved){
                        if(Objects.equals(s.getObjectId(), post.getObjectId())){
                            btnSaved.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.INVISIBLE);
                            return;
                        }
                        else {
                            btnSaved.setVisibility(View.INVISIBLE);
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });





            btnLike.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        btnLiked.setVisibility(View.VISIBLE);
                        btnLike.setVisibility(View.INVISIBLE);
                        relation.add(post);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            }
                        });
                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });

            btnLiked.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        btnLike.setVisibility(View.VISIBLE);
                        btnLiked.setVisibility(View.INVISIBLE);
                        relation.remove(post);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });

                        return super.onDoubleTap(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSaved.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                    savedRelation.add(post);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                        }
                    });
                }
            });
            btnSaved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSave.setVisibility(View.VISIBLE);
                    btnSaved.setVisibility(View.INVISIBLE);
                    savedRelation.remove(post);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                        }
                    });
                }
            });
        }
    }

}
