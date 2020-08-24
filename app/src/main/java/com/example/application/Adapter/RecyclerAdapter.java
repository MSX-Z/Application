package com.example.application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.ChatActivity;
import com.example.application.R;
import com.example.application.Retrieving_Data.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import nb.scode.lib.ExpandableTextView;

//import ru.embersoft.expandabletextview.ExpandableTextView;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<SumPostDistance> sumPostDistances;

    private String TAG = "RecyclerAdapter";

    public RecyclerAdapter(Context context, List<SumPostDistance> sumPostDistances){
        this.context = context;
        this.sumPostDistances = sumPostDistances;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_cardview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SumPostDistance item = sumPostDistances.get(position);
        Post post = item.getPost();
        holder.title.setText(post.getTitle());
        holder.name.setText(post.getName());
        holder.price.setText(post.getPrice()+"฿");
        holder.distance.setText(item.getDistance()+" Km.");

        holder.expandableTextView.setOnStateChangeListener(new ExpandableTextView.OnStateChangeListener() {
                    @Override public void onStateChange(boolean isShrink) {
                        SumPostDistance contentItem = sumPostDistances.get(position);
                        contentItem.setShrink(isShrink);
                        sumPostDistances.set(position, contentItem);
                    }
                });
        holder.expandableTextView.setText(post.getDesc());
        holder.expandableTextView.resetState(item.isShrink());

//        holder.distance.setText(distance.get(position)+" KM.");
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(layoutInflater.getContext(),DetailSearchActivity.class);
//                intent.putExtra("distance",list.get(position));
//                layoutInflater.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return sumPostDistances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView title,name,price,distance,property1;
        ExpandableTextView expandableTextView;
        Button contact;

        FirebaseAuth auth;

        String Uid;
        DatabaseReference ref;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            profile = itemView.findViewById(R.id.profile);

            title = itemView.findViewById(R.id.title);
            expandableTextView = itemView.findViewById(R.id.expandable_textview);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            distance = itemView.findViewById(R.id.distance);
            contact = itemView.findViewById(R.id.contact);

            property1 = itemView.findViewById(R.id.property1);

            auth = FirebaseAuth.getInstance();

            Uid = auth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference();

            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Profile "+sumPostDistances.get(getAdapterPosition()).getPost().getProfile_Url(), Toast.LENGTH_SHORT).show();
                }
            });

            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkUsersandContact();
//                    Toast.makeText(v.getContext(), "Click Contact : "+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Intent intent = new Intent(v.getContext(),DetailSearchActivity.class);
////                    intent.putExtra("distance",d.get(getAdapterPosition()));
////                    v.getContext().startActivity(intent);
//                }
//            });
        }

        private void checkUsersandContact() {
            Query query = ref.child("Users").child(Uid).child("position");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().equals("user")) {
                            context.startActivity(new Intent(context,ChatActivity.class));
                            Toast.makeText(context, "Click Contact : "+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "No User"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
