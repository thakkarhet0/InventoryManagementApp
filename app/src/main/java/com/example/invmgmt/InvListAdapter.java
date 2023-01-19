package com.example.invmgmt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class InvListAdapter extends RecyclerView.Adapter<InvListAdapter.UViewHolder> {

    ArrayList<Services.Inventory> invs;

    InvInterface am;

    FirebaseFirestore db;

    String uid;

    public InvListAdapter(ArrayList<Services.Inventory> invs, String uid) {
        this.invs = invs;
        this.uid = uid;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inv_list_item, parent, false);
        am = (InvInterface) parent.getContext();
        return new UViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {
        Services.Inventory inv = invs.get(position);
        holder.position = position;

        holder.name.setText(inv.getPro_name());
        holder.cost.setText(inv.getPro_cost());
        holder.quantity.setText(inv.getPro_quantity());
        holder.count.setText(inv.getPro_count() + "");


        if (!inv.getRef().equals("")) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid).child(inv.getRef());
            GlideApp.with(holder.rootView)
                    .load(storageReference)
                    .into(holder.pic);
        } else {
            holder.pic.setVisibility(View.GONE);
        }

        db = FirebaseFirestore.getInstance();

        holder.pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inv.incrCount();
                HashMap<String, Object> upd = new HashMap<>();
                upd.put("pro_count", inv.getPro_count() + "");
                am.toggleDialog(true);
                db.collection(Services.DB_USERS).document(uid).collection(Services.DB_INV).document(inv.getId()).update(upd).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        am.toggleDialog(false);
                        holder.count.setText(inv.getPro_count() + "");
                    }
                });
            }
        });

        holder.neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inv.decrCount();
                if (inv.getPro_count() <= 0) {
                    delInv(inv);
                    return;
                }
                HashMap<String, Object> upd = new HashMap<>();
                upd.put("pro_count", inv.getPro_count() + "");
                am.toggleDialog(true);
                db.collection(Services.DB_USERS).document(uid).collection(Services.DB_INV).document(inv.getId()).update(upd).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        am.toggleDialog(false);
                        holder.count.setText(inv.getPro_count() + "");
                    }
                });
            }
        });

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delInv(inv);
            }
        });


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.sendInvDetailFragment(inv);
            }
        });
    }

    public void delInv(Services.Inventory inv) {
        am.toggleDialog(true);
        DocumentReference dbc = db.collection(Services.DB_USERS).document(uid).collection(Services.DB_INV).document(inv.getId());
        dbc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                am.toggleDialog(false);
                if (!inv.getRef().equals("")) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid).child(inv.getRef());
                    storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                am.showAlertDialog("Inventory deleted!");
                            } else {
                                task.getException().printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.invs.size();
    }

    interface InvInterface {

        void sendInvDetailFragment(Services.Inventory inv);

        void toggleDialog(boolean show);

        void showAlertDialog(String msg);

    }

    public static class UViewHolder extends RecyclerView.ViewHolder {

        TextView name, cost, quantity, count;
        Button neg, pos;
        ImageView pic, del;
        View rootView;
        int position;

        public UViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            name = itemView.findViewById(R.id.textView);
            quantity = itemView.findViewById(R.id.textView2);
            cost = itemView.findViewById(R.id.textView3);
            count = itemView.findViewById(R.id.textView4);
            pos = itemView.findViewById(R.id.button3);
            neg = itemView.findViewById(R.id.button4);
            pic = itemView.findViewById(R.id.imageView);
            del = itemView.findViewById(R.id.imageView2);
        }

    }

}

