package com.example.invmgmt.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.invmgmt.GlideApp;
import com.example.invmgmt.MainActivity;
import com.example.invmgmt.R;
import com.example.invmgmt.Services;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.UUID;

public class AddInvFragment extends Fragment {

    private static final String INV = "inv";
    private static final int PICK_IMAGE = 100;
    private static final int CLICK_IMAGE = 101;
    EditText name, cost, count, quantity, details, supplier;
    IAIInt am;
    Button pick;
    ImageView preview;
    Services.Inventory inventory = null;
    Uri image_data = null;

    public static AddInvFragment newInstance(@Nullable Services.Inventory inventory) {
        AddInvFragment fragment = new AddInvFragment();
        if (inventory != null) {
            Bundle args = new Bundle();
            args.putSerializable(INV, inventory);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.inventory = (Services.Inventory) getArguments().getSerializable(INV);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAIInt) {
            am = (IAIInt) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != MainActivity.RESULT_OK) return;

        if (requestCode == PICK_IMAGE) {

            if (data != null) {
                image_data = data.getData();

                preview.setImageURI(image_data);
                preview.setVisibility(View.VISIBLE);
            } else {
                preview.setVisibility(View.GONE);
            }
        }

        if (requestCode == CLICK_IMAGE) {
            if (data.getExtras().get("data") != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                image_data = getImageUri(photo);

                preview.setImageURI(image_data);
                preview.setVisibility(View.VISIBLE);
            } else {
                preview.setVisibility(View.GONE);
            }
        }
    }

    private Uri getImageUri(Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_inv, container, false);
        getActivity().setTitle("Add New Inventory");

        Services.User user = am.getUser();

        name = view.findViewById(R.id.editTextTextPersonName);
        quantity = view.findViewById(R.id.editTextTextPersonName2);
        cost = view.findViewById(R.id.editTextTextPersonName3);
        count = view.findViewById(R.id.editTextTextPersonName4);
        supplier = view.findViewById(R.id.editTextTextPersonName5);
        details = view.findViewById(R.id.editTextTextPersonName6);
        preview = view.findViewById(R.id.imageView4);
        pick = view.findViewById(R.id.button6);

        preview.setVisibility(View.GONE);

        if (inventory != null) {
            name.setText(inventory.getPro_name());
            quantity.setText(inventory.getPro_quantity());
            cost.setText(inventory.getPro_cost());
            count.setText(inventory.getPro_count() + "");
            supplier.setText(inventory.getSupplier_name());
            details.setText(inventory.getDetails());
            if (!inventory.getRef().equals("")) {
                preview.setVisibility(View.VISIBLE);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child(inventory.getRef());
                GlideApp.with(view)
                        .load(storageReference)
                        .into(preview);
            }
        }

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] cs = {"Gallery", "Camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose")
                        .setItems(cs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                    startActivityForResult(gallery, PICK_IMAGE);
                                } else {
                                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(takePicture, CLICK_IMAGE);
                                }
                            }
                        }).show();
            }
        });

        view.findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pname = name.getText().toString();
                String pquantity = quantity.getText().toString();
                String pcost = cost.getText().toString();
                String pcount = count.getText().toString();
                String psupplier = supplier.getText().toString();
                String pdetails = details.getText().toString();

                if (pname.isEmpty() || pquantity.isEmpty() || pcost.isEmpty() || pcount.isEmpty() || psupplier.isEmpty()) {
                    am.showAlertDialog(getString(R.string.empty_fields));
                    return;
                }

                try {
                    int p_count = Integer.parseInt(pcount);
                    if (p_count < 1) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException exc) {
                    am.showAlertDialog("Product count should be a number!");
                    return;
                }

                HashMap<String, Object> inv = new HashMap<>();
                inv.put("pro_name", pname);
                inv.put("pro_cost", pcost);
                inv.put("details", pdetails);
                inv.put("pro_count", pcount + "");
                inv.put("pro_quantity", pquantity);
                inv.put("supplier_name", psupplier);
                if (inventory != null && !inventory.getRef().equals("")) {
                    inv.put("ref", inventory.getRef());
                } else {
                    inv.put("ref", "");
                }

                am.toggleDialog(true);
                if (image_data != null) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    if (!inv.get("ref").equals("")) {
                        storageReference.child(user.getUid()).child(inventory.getRef()).delete();
                    }
                    String file = UUID.randomUUID().toString() + ".jpg";
                    inv.put("ref", file);
                    storageReference.child(user.getUid()).child(file).putFile(image_data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            upload(inv, user.getUid());
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            am.toggleDialog(false);
                            am.showAlertDialog("Upload cancelled!");
                            am.sendHomeFragment();
                        }
                    });
                } else {
                    upload(inv, user.getUid());
                }

            }
        });

        view.findViewById(R.id.textView11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.sendHomeFragment();
            }
        });
        return view;
    }

    public void upload(HashMap<String, Object> inv, String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (inventory == null) {
            db.collection(Services.DB_USERS).document(uid).collection(Services.DB_INV).add(inv).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    am.toggleDialog(false);
                    if (task.isSuccessful()) {
                        am.showAlertDialog("Inventory upload successful!");
                        am.sendHomeFragment();
                    } else {
                        task.getException().printStackTrace();
                    }
                }
            });
        } else {
            db.collection(Services.DB_USERS).document(uid).collection(Services.DB_INV).document(inventory.getId()).update(inv).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    am.toggleDialog(false);
                    if (task.isSuccessful()) {
                        am.showAlertDialog("Inventory update successful!");
                        am.sendHomeFragment();
                    } else {
                        task.getException().printStackTrace();
                    }
                }
            });
        }
    }

    public interface IAIInt {

        void sendHomeFragment();

        void showAlertDialog(String msg);

        void toggleDialog(boolean show);

        @Nullable
        Services.User getUser();

    }

}