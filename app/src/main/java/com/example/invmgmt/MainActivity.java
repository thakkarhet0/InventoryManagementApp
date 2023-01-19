package com.example.invmgmt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invmgmt.fragment.AddInvFragment;
import com.example.invmgmt.fragment.HomeFragment;
import com.example.invmgmt.fragment.InvDetailFragment;
import com.example.invmgmt.fragment.LoginFragment;
import com.example.invmgmt.fragment.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SSDI 6112 Project - Inventory Management
 * Members:
 * Sneh Jain
 * Ivy Pham
 * Sakshi Shukla
 * Nick Debakey
 */
public class MainActivity extends AppCompatActivity implements HomeFragment.HInterface, InvDetailFragment.IIDInterface, AddInvFragment.IAIInt, InvListAdapter.InvInterface, LoginFragment.LoginInterface, RegisterFragment.RegInterface {

    ProgressDialog dialog;

    Services.User user = null;

    FirebaseAuth mAuth;

    public @Nullable
    Services.User getUser() {
        return user;
    }

    public void setUser(@Nullable Services.User user) {
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            sendLoginFragment();
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            setUser(new Services.User(user.getUid(), user.getDisplayName(), user.getEmail()));
            sendHomeFragment();
        }
    }

    public void sendAddInventoryFragment(@Nullable Services.Inventory inv, boolean addStack) {
        if (addStack) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, AddInvFragment.newInstance(inv))
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, AddInvFragment.newInstance(inv))
                    .commit();
        }
    }

    public void sendInvDetailFragment(Services.Inventory inv) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, InvDetailFragment.newInstance(inv))
                .addToBackStack(null)
                .commit();
    }

    public void sendLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, LoginFragment.newInstance())
                .commit();
    }

    public void sendHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new HomeFragment())
                .commit();
    }

    public void showAlertDialog(String alert) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert)
                .setMessage(alert)
                .setPositiveButton(R.string.okay, null)
                .show();
    }

    public void toggleDialog(boolean show) {
        if (show) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    public void sendRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, RegisterFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

}