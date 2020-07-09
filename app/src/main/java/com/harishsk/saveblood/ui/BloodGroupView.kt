package com.harishsk.saveblood.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.harishsk.saveblood.adapter.ListViewAdapter
import com.harishsk.saveblood.model.Blood
import com.harishsk.saveblood.model.User
import java.util.*

class BloodGroupView : Fragment() {

    var adapter: ListViewAdapter? = null
    var mreference: DatabaseReference? = null

    private lateinit var list_data: ListView
    private lateinit var progress: ProgressBar

    private val BlList: MutableList<Blood> = ArrayList()
    private var role: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_blood__group__view, container, false)
        list_data = view.findViewById(R.id.list_data)
        list_data.adapter = adapter
        progress = view.findViewById(R.id.progress)
        mreference = FirebaseDatabase.getInstance().reference
        mreference!!.keepSynced(true)
        progress.visibility = View.VISIBLE
        list_data.visibility = View.INVISIBLE
        list_data.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                opendialog(
                    parent,
                    view,
                    position,
                    id
                )
            }
        addValueEventListener()
        addValueEventListner()
        return view
    }

    private fun opendialog(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ) {
        val name = view.findViewById<TextView>(R.id.lt_name)
        val gender = view.findViewById<TextView>(R.id.lt_gender)
        val age = view.findViewById<TextView>(R.id.lt_age)
        val blgrp = view.findViewById<TextView>(R.id.lt_blgrp)
        val phno = view.findViewById<TextView>(R.id.lt_phno)
        val place = view.findViewById<TextView>(R.id.lt_place)
        try {
            val build =
                AlertDialog.Builder(activity!!)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialogview, null)
            build.setView(dialogView)
            val name1 = dialogView.findViewById<TextView>(R.id.lt_name)
            val gender1 = dialogView.findViewById<TextView>(R.id.lt_gender)
            val age1 = dialogView.findViewById<TextView>(R.id.lt_age)
            val blgrp1 = dialogView.findViewById<TextView>(R.id.lt_blgrp)
            val phno1 = dialogView.findViewById<TextView>(R.id.lt_phno)
            val place1 = dialogView.findViewById<TextView>(R.id.lt_place)
            val Sname = name.text.toString().trim { it <= ' ' }
            val Sgender = gender.text.toString().trim { it <= ' ' }
            val Sage = age.text.toString().trim { it <= ' ' }
            val Sblgrp = blgrp.text.toString().trim { it <= ' ' }
            val Sphno = phno.text.toString().trim { it <= ' ' }
            val Splace = place.text.toString().trim { it <= ' ' }
            name1.text = Sname
            gender1.text = Sgender
            age1.text = Sage
            blgrp1.text = Sblgrp
            phno1.text = Sphno
            place1.text = Splace
            build.setCancelable(true)
            build.setTitle("Donor Details")
            build.setPositiveButton(
                "CALL"
            ) { arg0: DialogInterface?, arg1: Int ->
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$Sphno")))
            }
            build.setNegativeButton(
                "SHARE"
            ) { dialog: DialogInterface?, arg1: Int ->
                val share = Intent(Intent.ACTION_SEND_MULTIPLE)
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    """
                ${getString(R.string.donar_name)}$Sname
                ${getString(R.string.donor_gender)}$Sgender
                ${getString(R.string.donor_age)}$Sage
                ${getString(R.string.donor_blood_group)}$Sblgrp
                ${getString(R.string.donor_phone_number)}$Sphno
                ${getString(R.string.donor_place)}$Splace
                """.trimIndent()
                )
                share.type = getString(R.string.type_text)
                startActivity(Intent.createChooser(share, getString(R.string.share_data)))
            }
            build.setNeutralButton(
                "Delete"
            ) { dialog: DialogInterface?, which: Int ->
                removevalue(Sphno)
            }
            val alert = build.create()
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addValueEventListner() {
        mreference!!.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val user =
                        dataSnapshot.getValue(
                            User::class.java
                        )
                    if (user != null) role = user.role
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(
                        TAG,
                        "Error Initial:$databaseError"
                    )
                    Toast.makeText(activity, "User Not Found", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun removevalue(s: String) {
        if (role == getString(R.string.admin)) mreference!!.child("Blood/$s")
            .removeValue { databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                Toast.makeText(
                    activity,
                    R.string.data_removed.toString() + s,
                    Toast.LENGTH_SHORT
                ).show()
                Log.i(
                    TAG,
                    s + " " + getString(R.string.data_removed)
                )
            } else {
            Toast.makeText(activity, R.string.admin_permission, Toast.LENGTH_SHORT).show()
            Log.i(
                TAG,
                "Error removing $s Role: $role"
            )
        }
    }

    private fun addValueEventListener() {
        mreference!!.child("Blood").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (BlList.size > 0) BlList.clear()
                for (postSnapshot in dataSnapshot.children) BlList.add(
                    postSnapshot.getValue(
                        Blood::class.java
                    )!!
                )
                adapter = ListViewAdapter(activity, BlList)
                list_data.adapter = adapter
                progress.visibility = View.INVISIBLE
                list_data.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseerror: DatabaseError) {
                Toast.makeText(
                    activity,
                    getString(R.string.Server_Page_refresh),
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(
                    TAG,
                    "getUser:onCancelled",
                    databaseerror.toException()
                )
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        inflater.inflate(R.menu.dashboard, menu)
        val myActionMenuItem = menu.findItem(R.id.action_search)
        val searchView =
            MenuItemCompat.getActionView(myActionMenuItem) as SearchView
        searchView.queryHint = getString(R.string.search_here)
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                FirebaseSearch(query.toUpperCase())
                return false
            }
        })
    }

    private fun FirebaseSearch(query: String) {
        val fbquery =
            mreference!!.child("Blood").orderByChild("blgrp").startAt(query).endAt(query + "\uf8ff")
        Log.i(TAG, getString(R.string.Log_Query) + query)
        fbquery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (BlList.size > 0) BlList.clear()
                for (postSnapshot in dataSnapshot.children) BlList.add(
                    postSnapshot.getValue(
                        Blood::class.java
                    )!!
                )
                if (BlList.size <= 0) Toast.makeText(
                    activity,
                    R.string.No_Data_Found,
                    Toast.LENGTH_SHORT
                ).show()
                adapter = ListViewAdapter(activity, BlList)
                list_data.adapter = adapter
            }

            override fun onCancelled(databaseerror: DatabaseError) {
                Toast.makeText(
                    activity,
                    getString(R.string.Server_Page_refresh),
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(
                    TAG,
                    "getUser:onCancelled",
                    databaseerror.toException()
                )
            }
        })
    }

    companion object {
        private val TAG = BloodGroupView::class.java.simpleName
    }
}