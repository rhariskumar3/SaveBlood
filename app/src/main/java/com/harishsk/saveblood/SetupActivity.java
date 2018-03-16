package com.harishsk.saveblood;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    String name, phone, role, Sname;
    private String TAG = SetupActivity.class.getSimpleName();
    private EditText setupName;
    private TextView TVrole, TVphone;
    private Button setupBtn, updateuserbtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        setupName = findViewById(R.id.setup_name);
        Sname = setupName.getText().toString().trim();
        setupBtn = findViewById(R.id.setup_submit_btn);
        setupBtn.setOnClickListener(this);
        updateuserbtn = findViewById(R.id.setup_user_update_btn);
        updateuserbtn.setOnClickListener(this);
        TVrole = findViewById(R.id.setup_role);
        TVphone = findViewById(R.id.setup_phone);

        //Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mreference = FirebaseDatabase.getInstance().getReference("Users");
        mreference.keepSynced(true);
        welcome();
        addValueEventListner();
    }

    public void welcome() {
        if (mUser != null) {
            name = mUser.getDisplayName();
            phone = mUser.getPhoneNumber();
        } else
            sendToAuth();

    }

    public void addValueEventListner() {
        mreference.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getName() != null && user.getRole() != null) {
                        setupName.setText(user.getName());
                        TVphone.setText(phone);
                        TVrole.setText(user.getRole());
                        updateview(user.getRole());
                    } else {
                        setupName.setHint(R.string.user);
                        TVphone.setText(phone);
                        TVrole.setText(getString(R.string.user));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error Initial:" + databaseError);
                Toast.makeText(SetupActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateview(String role) {
        if (!role.equals("ADMIN"))
            updateuserbtn.setVisibility(View.GONE);
        else
            updateuserbtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setup_submit_btn)
            firebasedatupdate();
        else if (view.getId() == R.id.setup_user_update_btn)
            display_alert();
    }

    private void firebasedatupdate() {
        if (validate()) {
            DatabaseReference mreference = FirebaseDatabase.getInstance().getReference("Users");
            User fh = new User(name, phone, role);
            mreference.child(phone).setValue(fh).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SetupActivity.this, R.string.data_added, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, getString(R.string.data_added));
                    sendToMain();
                } else {
                    Toast.makeText(SetupActivity.this, R.string.data_not_added, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, getString(R.string.data_not_added) + task.getException().getMessage());
                }
            });
        } else
            Toast.makeText(this, R.string.add_required, Toast.LENGTH_SHORT).show();
    }

    private void gatherdata() {
        name = setupName.getText().toString().trim();
        role = TVrole.getText().toString().trim();
    }

    private boolean validate() {
        gatherdata();
        if (role.isEmpty())
            role = "USER";
        return TextUtils.isEmpty(Sname);
    }

    private void display_alert() {
        try {
            final AlertDialog.Builder build = new AlertDialog.Builder(this);

            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialogview_user, null);
            build.setView(dialogView);

            final EditText phoneno = dialogView.findViewById(R.id.dvu_phno);
            Spinner role = dialogView.findViewById(R.id.dvu_role);

            List<String> list = new ArrayList<>();
            list.add("USER");
            list.add("ADMIN");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            role.setAdapter(dataAdapter);

            build.setTitle("Update User Role");

            build.setPositiveButton("UPDATE", (arg0, arg1) -> validate_userupdate(phoneno.getText().toString().trim(), role.getSelectedItem().toString()));

            build.setNegativeButton("CANCEL", (dialog, arg1) -> dialog.dismiss());
            AlertDialog alert = build.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Alert Alert" + e.getMessage());
        }
    }

    private void validate_userupdate(String uph, String ur) {
        if (TextUtils.isEmpty(uph))
            Toast.makeText(this, R.string.add_required, Toast.LENGTH_SHORT).show();
        else
            verify_user(uph, ur);
    }

    private void updateuser(String uph, String ur) {
        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference("Users");
        mreference.child("+91" + uph).child("role").setValue(ur).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "User " + uph + " Role Updated to " + ur);
                Toast.makeText(getApplicationContext(), "Role Updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "User " + uph + " Role Not Updated to " + ur);
                Toast.makeText(getApplicationContext(), "Role Not Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verify_user(String uph, String ur){
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (
                        dataSnapshot.hasChild("+91" + uph))
                    updateuser(uph, ur);
                else
                    Toast.makeText(getApplicationContext(), "User Not Found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error: " + databaseError);
                Toast.makeText(getApplicationContext(), "Error: " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;
            default:
                return false;
        }
    }
    private void logout() {
        mAuth.signOut();
        if (mAuth.getCurrentUser() == null)
            sendToAuth();
    }

    private void sendToAuth() {
        startActivity(new Intent(this, PhoneAuthActivity.class));
        finish();
    }

    private void sendToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
