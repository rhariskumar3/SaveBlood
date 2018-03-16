package com.harishsk.saveblood;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Blood_Group_View extends Fragment {

    private static final String TAG = Blood_Group_View.class.getSimpleName();
    ListViewAdapter adapter;
    DatabaseReference mreference;
    private ListView list_data;
    private ProgressBar progress;
    private List<Blood> BlList = new ArrayList<>();
    private String role;

    public Blood_Group_View() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood__group__view, container, false);

        list_data = view.findViewById(R.id.list_data);
        list_data.setAdapter(adapter);
        progress = view.findViewById(R.id.progress);

        mreference = FirebaseDatabase.getInstance().getReference();
        mreference.keepSynced(true);

        progress.setVisibility(View.VISIBLE);
        list_data.setVisibility(View.INVISIBLE);

        list_data.setOnItemClickListener(this::opendialog);
        addValueEventListener();
        addValueEventListner();

        return view;
    }

    private void opendialog(AdapterView<?> parent, View view, int position, long id) {
        TextView name = view.findViewById(R.id.lt_name);
        TextView gender = view.findViewById(R.id.lt_gender);
        TextView age = view.findViewById(R.id.lt_age);
        TextView blgrp = view.findViewById(R.id.lt_blgrp);
        TextView phno = view.findViewById(R.id.lt_phno);
        TextView place = view.findViewById(R.id.lt_place);

        try {
            final AlertDialog.Builder build = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialogview, null);
            build.setView(dialogView);

            TextView name1 = dialogView.findViewById(R.id.lt_name);
            TextView gender1 = dialogView.findViewById(R.id.lt_gender);
            TextView age1 = dialogView.findViewById(R.id.lt_age);
            TextView blgrp1 = dialogView.findViewById(R.id.lt_blgrp);
            TextView phno1 = dialogView.findViewById(R.id.lt_phno);
            TextView place1 = dialogView.findViewById(R.id.lt_place);

            String Sname = name.getText().toString().trim();
            String Sgender = gender.getText().toString().trim();
            String Sage = age.getText().toString().trim();
            String Sblgrp = blgrp.getText().toString().trim();
            String Sphno = phno.getText().toString().trim();
            String Splace = place.getText().toString().trim();

            name1.setText(Sname);
            gender1.setText(Sgender);
            age1.setText(Sage);
            blgrp1.setText(Sblgrp);
            phno1.setText(Sphno);
            place1.setText(Splace);

            build.setCancelable(true);
            build.setTitle("Donor Details");

            build.setPositiveButton("CALL", (arg0, arg1) -> {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Sphno)));
            });

            build.setNegativeButton("SHARE", (dialog, arg1) -> {
                Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
                share.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.donar_name) + Sname + "\n" +
                                getString(R.string.donor_gender) + Sgender + "\n" +
                                getString(R.string.donor_age) + Sage + "\n" +
                                getString(R.string.donor_blood_group) + Sblgrp + "\n" +
                                getString(R.string.donor_phone_number) + Sphno + "\n" +
                                getString(R.string.donor_place) + Splace);
                share.setType(getString(R.string.type_text));
                startActivity(Intent.createChooser(share, getString(R.string.share_data)));
            });

            build.setNeutralButton("Delete", (dialog, which) -> {
                removevalue(Sphno);
            });
            AlertDialog alert = build.create();
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addValueEventListner() {
        mreference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                if (user != null) role = user.getRole();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error Initial:" + databaseError);
                Toast.makeText(getActivity(), "User Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removevalue(String s) {
        if (role.equals(getString(R.string.admin)))
            mreference.child("Blood/" + s).removeValue((databaseError, databaseReference) -> {
                Toast.makeText(getActivity(), R.string.data_removed + s, Toast.LENGTH_SHORT).show();
                Log.i(TAG, s + " " + getString(R.string.data_removed));
            });
        else {
            Toast.makeText(getActivity(), R.string.admin_permission, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Error removing " + s + " Role: " + role);
        }
    }

    private void addValueEventListener() {
        mreference.child("Blood").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (BlList.size() > 0)
                    BlList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    BlList.add(postSnapshot.getValue(Blood.class));
                adapter = new ListViewAdapter(getActivity(), BlList);
                list_data.setAdapter(adapter);

                progress.setVisibility(View.INVISIBLE);
                list_data.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseerror) {
                Toast.makeText(getActivity(), getString(R.string.Server_Page_refresh), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "getUser:onCancelled", databaseerror.toException());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_here));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                FirebaseSearch(query.toUpperCase());
                return false;
            }
        });
    }

    private void FirebaseSearch(String query) {

        Query fbquery = mreference.child("Blood").orderByChild("blgrp").startAt(query).endAt(query + "\uf8ff");
        Log.i(TAG, getString(R.string.Log_Query) + query);

        fbquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (BlList.size() > 0)
                    BlList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    BlList.add(postSnapshot.getValue(Blood.class));
                if (BlList.size() <= 0)
                    Toast.makeText(getActivity(), R.string.No_Data_Found, Toast.LENGTH_SHORT).show();
                adapter = new ListViewAdapter(getActivity(), BlList);
                list_data.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseerror) {
                Toast.makeText(getActivity(), getString(R.string.Server_Page_refresh), Toast.LENGTH_SHORT).show();
                Log.w(TAG, "getUser:onCancelled", databaseerror.toException());
            }
        });
    }
}
