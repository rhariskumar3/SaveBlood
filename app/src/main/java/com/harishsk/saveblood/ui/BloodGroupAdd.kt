package com.harishsk.saveblood.ui

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.harishsk.saveblood.Blood_Group_View
import com.harishsk.saveblood.R
import com.harishsk.saveblood.model.Blood
import java.util.*

class BloodGroupAdd : Fragment() {
    var name: EditText? = null
    var age: EditText? = null
    var phno: EditText? = null
    var place: EditText? = null
    var Sname: String? = null
    var Sage: String? = null
    var Sphno: String? = null
    var Splace: String? = null
    var Sgender: String? = null
    var Sblgrp: String? = null
    var gender: Spinner? = null
    var blgrp: Spinner? = null
    var save: Button? = null
    var clear: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_blood__group__add, container, false)
        name = view.findViewById(R.id.txtName)
        gender = view.findViewById(R.id.spinner_gender)
        age = view.findViewById(R.id.txtAge)
        blgrp = view.findViewById(R.id.spinner_blgrp)
        phno = view.findViewById(R.id.txtphno)
        place = view.findViewById(R.id.txtPlace)
        save = view.findViewById(R.id.btnSave)
        clear = view.findViewById(R.id.btnClear)
        spinnerdata()
        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                save.setEnabled(false)
                clear.setEnabled(false)
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                save.setEnabled(true)
                clear.setEnabled(true)
            }
        })
        save.setOnClickListener(View.OnClickListener { v: View? -> validate() })
        clear.setOnClickListener(View.OnClickListener { v: View? -> ClearAlert() })
        return view
    }

    private fun ClearAlert() {
        val build =
            AlertDialog.Builder(activity!!)
        build.setTitle("Do you want to clear the entered details?")
        build.setPositiveButton(
            "YES"
        ) { dialog: DialogInterface?, arg1: Int -> ClearData() }
        build.setNegativeButton(
            "CANCEL"
        ) { arg0: DialogInterface, arg1: Int -> arg0.cancel() }
        val alert = build.create()
        alert.show()
    }

    private fun ClearData() {
        name!!.setText("")
        gender!!.setSelection(0)
        age!!.setText("")
        blgrp!!.setSelection(0)
        phno!!.setText("")
        place!!.setText("")
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = phno!!.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            phno!!.error = "Invalid phone number."
            return false
        }
        return true
    }

    private fun validate() {
        geatherdata()
        if (TextUtils.isEmpty(Sname) && TextUtils.isEmpty(Sage) && TextUtils.isEmpty(Sphno) && TextUtils.isEmpty(
                Splace
            )
        ) {
            name!!.error = "Required"
            age!!.error = "Required"
            if (!validatePhoneNumber()) phno!!.error = "Required"
            place!!.error = "Required"
        } else if (TextUtils.isEmpty(Sname)) firebasedatupdate()
    }

    private fun firebasedatupdate() {
        geatherdata()
        val mreference = FirebaseDatabase.getInstance().getReference("Blood")
        val userid = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        val fh = Blood(Sname, Sgender, Sage, Sblgrp, Sphno, Splace, userid)
        if (validatePhoneNumber()) {
            mreference.child(Sphno!!).setValue(fh)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, R.string.data_added, Toast.LENGTH_SHORT)
                            .show()
                        Log.i(
                            TAG,
                            getString(R.string.data_added)
                        )
                        ClearData()
                        openBloodGroupView()
                    } else {
                        Toast.makeText(activity, R.string.data_not_added, Toast.LENGTH_SHORT)
                            .show()
                        Log.i(
                            TAG,
                            getString(R.string.data_not_added) + task.exception!!.message
                        )
                    }
                }
        }
    }

    private fun spinnerdata() {
        val list: MutableList<String> =
            ArrayList()
        list.add("Male")
        list.add("Female")
        dataAdapter_gen =
            ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, list)
        dataAdapter_gen!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gender!!.adapter = dataAdapter_gen
        val list1: MutableList<String> =
            ArrayList()
        list1.add("O+")
        list1.add("A+")
        list1.add("B+")
        list1.add("AB+")
        list1.add("O-")
        list1.add("A-")
        list1.add("B-")
        list1.add("AB-")
        list1.add("A1+")
        list1.add("A1-")
        list1.add("B1+")
        list1.add("B1-")
        list1.add("A2+")
        list1.add("A2-")
        list1.add("B2+")
        list1.add("B2-")
        list1.add("A1B+")
        list1.add("A1B-")
        list1.add("A2B+")
        list1.add("A2B-")
        list1.add("AABB+")
        list1.add("AABB-")
        dataAdapter_blgrp =
            ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, list1)
        dataAdapter_blgrp!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        blgrp!!.adapter = dataAdapter_blgrp
    }

    fun geatherdata() {
        Sname = name!!.text.toString().trim { it <= ' ' }
        Sgender = gender!!.selectedItem.toString()
        Sage = age!!.text.toString().trim { it <= ' ' }
        Sblgrp = blgrp!!.selectedItem.toString()
        Sphno = phno!!.text.toString().trim { it <= ' ' }
        Splace = place!!.text.toString().trim { it <= ' ' }
    }

    private fun openBloodGroupView() {
        val nextFrag = BloodGroupView()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, nextFrag)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private val TAG = BloodGroupAdd::class.java.simpleName
        var dataAdapter_gen: ArrayAdapter<String>? = null
        var dataAdapter_blgrp: ArrayAdapter<String>? = null
    }
}