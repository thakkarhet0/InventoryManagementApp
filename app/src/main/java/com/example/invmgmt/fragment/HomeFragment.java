package com.example.invmgmt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invmgmt.InvListAdapter;
import com.example.invmgmt.R;
import com.example.invmgmt.Services;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    HInterface am;
    RecyclerView inv_list;
    LinearLayoutManager llm;
    InvListAdapter ila;
    private FirebaseAuth mAuth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HInterface) {
            am = (HInterface) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Services.User user = am.getUser();
        getActivity().setTitle(user.getDisplay_name() + "'s Inventory");
        mAuth = FirebaseAuth.getInstance();

        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                am.setUser(null);
                am.sendLoginFragment();
            }
        });

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.sendAddInventoryFragment(null, true);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Services.DB_USERS).document(user.getUid()).collection(Services.DB_INV).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value == null) {
                    return;
                }
                user.clearInventory();
                for (QueryDocumentSnapshot doc : value) {
                    Services.Inventory inv = new Services.Inventory();
                    inv.setId(doc.getId());
                    inv.setPro_name(doc.getString("pro_name"));
                    inv.setPro_count(Integer.parseInt(doc.getString("pro_count")));
                    inv.setPro_cost(doc.getString("pro_cost"));
                    inv.setPro_quantity(doc.getString("pro_quantity"));
                    inv.setDetails(doc.getString("details"));
                    inv.setSupplier_name(doc.getString("supplier_name"));
                    inv.setRef(doc.getString("ref"));

                    user.addInventory(inv);
                }
                ila.notifyDataSetChanged();
            }
        });

        inv_list = view.findViewById(R.id.inv_list);
        inv_list.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        inv_list.setLayoutManager(llm);
        ila = new InvListAdapter(user.getInventory(), user.getUid());
        inv_list.setAdapter(ila);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(inv_list.getContext(),
                llm.getOrientation());
        inv_list.addItemDecoration(dividerItemDecoration);

        return view;
    }

    public interface HInterface {

        @Nullable
        Services.User getUser();

        void setUser(@Nullable Services.User user);

        void sendLoginFragment();

        void sendAddInventoryFragment(@Nullable Services.Inventory inv, boolean addStack);

    }

}