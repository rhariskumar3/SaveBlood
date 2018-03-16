package com.harishsk.saveblood;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    FragmentTransaction transaction;
    private BottomNavigationView navigation;
    private FirebaseAuth mAuth;
    private String uid;
    private DatabaseReference mreference;
    private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    {
        mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadfragment(new HomeFragment());
                    return true;
                case R.id.navigation_add:
                    loadfragment(new Blood_Group_Add());
                    return true;
                case R.id.navigation_view:
                    loadfragment(new Blood_Group_View());
                    return true;
            }
            return false;
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar();

        loadfragment(new HomeFragment());
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //Firbase
        initfirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) sendToLogin();
        else
            uid = mAuth.getCurrentUser().getPhoneNumber();

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uid)) {
                    startActivity(new Intent(MainActivity.this, SetupActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error: " + databaseError);
                Toast.makeText(MainActivity.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initfirebase() {
        FirebaseApp.initializeApp(this);
        mreference = FirebaseDatabase.getInstance().getReference();
        mreference.keepSynced(true);
    }

    void loadfragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_btn:
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void sendToLogin() {
        startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.click_back_again, Toast.LENGTH_SHORT).show();
        Log.i(TAG, getString(R.string.click_back_again));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
