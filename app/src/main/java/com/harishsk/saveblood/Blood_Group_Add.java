package com.harishsk.saveblood;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Blood_Group_Add extends Fragment {

    private static final String TAG = Blood_Group_Add.class.getSimpleName();
    public static ArrayAdapter<String> dataAdapter_gen, dataAdapter_blgrp;
    EditText name, age, phno, place;
    String Sname, Sage, Sphno, Splace, Sgender, Sblgrp;
    Spinner gender, blgrp;
    Button save, clear;

    public Blood_Group_Add() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood__group__add, container, false);

        name = view.findViewById(R.id.txtName);
        gender = view.findViewById(R.id.spinner_gender);
        age = view.findViewById(R.id.txtAge);
        blgrp = view.findViewById(R.id.spinner_blgrp);
        phno = view.findViewById(R.id.txtphno);
        place = view.findViewById(R.id.txtPlace);
        save = view.findViewById(R.id.btnSave);
        clear = view.findViewById(R.id.btnClear);

        spinnerdata();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                save.setEnabled(false);
                clear.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                save.setEnabled(true);
                clear.setEnabled(true);
            }
        });
        save.setOnClickListener(v -> validate());
        clear.setOnClickListener(v -> ClearAlert());

        return view;
    }

    private void ClearAlert() {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle("Do you want to clear the entered details?");
        build.setPositiveButton("YES", (dialog, arg1) -> ClearData());
        build.setNegativeButton("CANCEL", (arg0, arg1) -> arg0.cancel());
        AlertDialog alert = build.create();
        alert.show();
    }

    private void ClearData() {
        name.setText("");
        gender.setSelection(0);
        age.setText("");
        blgrp.setSelection(0);
        phno.setText("");
        place.setText("");
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phno.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phno.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void validate() {

        geatherdata();

        if (TextUtils.isEmpty(Sname) && TextUtils.isEmpty(Sage) && TextUtils.isEmpty(Sphno) && TextUtils.isEmpty(Splace)) {
            name.setError("Required");
            age.setError("Required");
            if (!validatePhoneNumber())
                phno.setError("Required");
            place.setError("Required");
        } else
            if (TextUtils.isEmpty(Sname))
                firebasedatupdate();
    }

    private void firebasedatupdate() {
        geatherdata();
        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference("Blood");
        String userid = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Blood fh = new Blood(Sname, Sgender, Sage, Sblgrp, Sphno, Splace, userid);
        if (validatePhoneNumber()){
            mreference.child(Sphno).setValue(fh).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), R.string.data_added, Toast.LENGTH_SHORT).show();
                Log.i(TAG, getString(R.string.data_added));
                ClearData();
                openBloodGroupView();
            } else {
                Toast.makeText(getActivity(), R.string.data_not_added, Toast.LENGTH_SHORT).show();
                Log.i(TAG, getString(R.string.data_not_added) + task.getException().getMessage());
            }
        });}
    }

    private void spinnerdata() {
        List<String> list = new ArrayList<>();
        list.add("Male");
        list.add("Female");
        dataAdapter_gen = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter_gen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter_gen);

        List<String> list1 = new ArrayList<>();
        list1.add("O+");
        list1.add("A+");
        list1.add("B+");
        list1.add("AB+");
        list1.add("O-");
        list1.add("A-");
        list1.add("B-");
        list1.add("AB-");
        list1.add("A1+");
        list1.add("A1-");
        list1.add("B1+");
        list1.add("B1-");
        list1.add("A2+");
        list1.add("A2-");
        list1.add("B2+");
        list1.add("B2-");
        list1.add("A1B+");
        list1.add("A1B-");
        list1.add("A2B+");
        list1.add("A2B-");
        list1.add("AABB+");
        list1.add("AABB-");
        dataAdapter_blgrp = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list1);
        dataAdapter_blgrp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blgrp.setAdapter(dataAdapter_blgrp);
    }

    void geatherdata() {
        Sname = name.getText().toString().trim();
        Sgender = gender.getSelectedItem().toString();
        Sage = age.getText().toString().trim();
        Sblgrp = blgrp.getSelectedItem().toString();
        Sphno = phno.getText().toString().trim();
        Splace = place.getText().toString().trim();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openBloodGroupView(){
        Blood_Group_View nextFrag= new Blood_Group_View();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, nextFrag)
                .addToBackStack(null)
                .commit();
    }
}
