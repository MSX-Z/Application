package com.example.application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class PostMeRecyclerAdapter extends RecyclerView.Adapter<PostMeRecyclerAdapter.ViewHolder> {

    private Context context;

    public PostMeRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PostMeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostMeRecyclerAdapter.ViewHolder holder, int position) {
        holder.distance.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            distance = itemView.findViewById(R.id.distance);
        }
    }
}
