package com.example.invmgmt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invmgmt.GlideApp;
import com.example.invmgmt.R;
import com.example.invmgmt.Services;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InvDetailFragment extends Fragment {

    private static final String INV = "inv";
    TextView name, cost, quantity, count, supplier, details;
    ImageView pic;
    IIDInterface am;
    private Services.Inventory inv;

    public static InvDetailFragment newInstance(Services.Inventory inv) {
        InvDetailFragment fragment = new InvDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(INV, inv);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IIDInterface) {
            am = (IIDInterface) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.inv = (Services.Inventory) getArguments().getSerializable(INV);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inv_detail, container, false);
        getActivity().setTitle("Inventory Details");
        pic = view.findViewById(R.id.imageView3);
        Services.User user = am.getUser();
        if (!inv.getRef().equals("")) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child(inv.getRef());
            GlideApp.with(view)
                    .load(storageReference)
                    .into(pic);
        } else {
            pic.setVisibility(View.GONE);
        }

        name = view.findViewById(R.id.textView13);
        count = view.findViewById(R.id.textView14);
        quantity = view.findViewById(R.id.textView15);
        cost = view.findViewById(R.id.textView16);
        supplier = view.findViewById(R.id.textView17);
        details = view.findViewById(R.id.textView18);

        name.setText(inv.getPro_name());
        cost.setText(inv.getPro_cost());
        count.setText(inv.getPro_count() + "");
        quantity.setText(inv.getPro_quantity());
        supplier.setText(inv.getSupplier_name());
        details.setText(inv.getDetails());

        view.findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.sendAddInventoryFragment(inv, false);
            }
        });

        view.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.sendHomeFragment();
            }
        });

        return view;
    }

    public interface IIDInterface {

        @Nullable
        Services.User getUser();

        void sendHomeFragment();

        void sendAddInventoryFragment(@Nullable Services.Inventory inv, boolean addStack);

    }

}