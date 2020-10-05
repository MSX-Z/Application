package com.example.application.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.AddAddressActivity;
import com.example.application.R;
import com.example.application.Retrieving_Data.Address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class AddressRecyclerAdapter extends RecyclerView.Adapter<AddressRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Address> addresses;

    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private String Uid;
    private DatabaseReference ref;

    public AddressRecyclerAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.address,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address item = addresses.get(position);
        holder.radiobutton.setChecked(item.isUsability());
        holder.name.setText(item.getName());
        holder.phone.setText(item.getPhone());
        holder.address.setText(convertAddressForm(item));
    }

    private String convertAddressForm(Address a){
        String T = "";
        String []ad = a.getAddress().split( ":");
        for(String i : ad)
            T += i+" ";
        return T.trim();
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RadioButton radiobutton;
        TextView name,phone,address;
        ImageButton edit;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            radiobutton = itemView.findViewById(R.id.radiobutton);

            name = itemView.findViewById(R.id.name);
            edit = itemView.findViewById(R.id.edit);

            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);

            progressDialog = new ProgressDialog(context);

            auth = FirebaseAuth.getInstance();
            Uid = auth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference();

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,AddAddressActivity.class);
                    intent.putExtra("selectedItem", addresses.get(getAdapterPosition()));
                    context.startActivity(intent);
                    Log.d("TAG", "onClick: "+addresses.get(getAdapterPosition()).getKey());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Changing...");
                    progressDialog.show();
                    checkYourAddress(addresses.get(getAdapterPosition()));
                }
            });
        }

        private void checkYourAddress(final Address addressPosition) {
            Query query = ref.child("Address_Books").child(Uid).orderByChild("usability").equalTo(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ds.getRef().child("usability").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.setMessage("Update address...");
                                    Log.d("TAG", "onSuccess: ");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    }
                    selectYourAddress(addressPosition);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void selectYourAddress(Address adapterPosition) {
            String key = adapterPosition.getKey();
            final String address = adapterPosition.getAddress();
            HashMap<String,Object> usability = new HashMap<String, Object>();
            usability.put("usability",true);
            ref.child("Address_Books").child(Uid).child(key).updateChildren(usability).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "onSuccess: ");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    },1000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onFailure: "+e.getMessage());
                }
            });
        }
    }
}
