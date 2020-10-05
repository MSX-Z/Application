package com.example.application.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.R;
import com.example.application.Retrieving_Data.Chat;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;
    private String profile_Url;
    private String keyRoom;

    private FirebaseAuth auth;

    public ChatRecyclerAdapter(Context context, List<Chat> chats, String profile_Url,String keyRoom) {
        this.context = context;
        this.chats = chats;
        this.profile_Url = profile_Url;
        this.keyRoom = keyRoom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == MSG_TYPE_LEFT)
            view = LayoutInflater.from(context).inflate(R.layout.chat_left,parent,false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.chat_right,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Chat items = chats.get(position);
        String Message = items.getMessage();
        String Time = items.getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(Time));
        String date = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.message.setText(Message);
        holder.time.setText(date);

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                Log.d("TAG", "onLongClick: ");
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete message");
                builder.setMessage("Are you sure to delete this message ?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(v,position);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

        if(position == chats.size()-1){
            if(chats.get(position).isSeen()) {
                holder.seen.setText("Seen.");
            }
            else {
                holder.seen.setText("Delivered.");
            }
        }else{
            holder.seen.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(final View v, int position) {
        final String Uid = auth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp = chats.get(position).getTimestamp();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chat");
        Query query = ref.child(keyRoom).orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()) {
                    if (ds.child("sender").getValue().equals(Uid)) {
                        ds.getRef().removeValue();
                        Snackbar snackbar = Snackbar.make(v,"Message was deleted successfully.",Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        auth = FirebaseAuth.getInstance();
        if(chats.get(position).getSender().equals(auth.getCurrentUser().getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout messageLayout;
        CircularImageView profile;
        TextView message, seen, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageLayout = itemView.findViewById(R.id.messageLayout);

            profile = itemView.findViewById(R.id.profile);

            message = itemView.findViewById(R.id.message);
            seen = itemView.findViewById(R.id.seen);
            time = itemView.findViewById(R.id.time);

        }
    }
}
