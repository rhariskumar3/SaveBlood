package com.harishsk.saveblood.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.harishsk.saveblood.R
import com.harishsk.saveblood.model.User
import java.util.*

class SetupActivity : AppCompatActivity(), View.OnClickListener {

    var name: String? = ""
    var phone: String? = ""
    var role: String? = ""
    var Sname: String? = ""

    private val TAG = SetupActivity::class.java.simpleName

    private lateinit var setupName: EditText
    private lateinit var TVrole: TextView
    private lateinit var TVphone: TextView
    private lateinit var setupBtn: Button
    private lateinit var updateuserbtn: Button

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mUser: FirebaseUser? by lazy {
        mAuth.currentUser
    }
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        setupName = findViewById(R.id.setup_name)
        Sname = setupName.text.toString().trim { it <= ' ' }
        setupBtn = findViewById(R.id.setup_submit_btn)
        setupBtn.setOnClickListener(this)
        updateuserbtn = findViewById(R.id.setup_user_update_btn)
        updateuserbtn.setOnClickListener(this)
        TVrole = findViewById(R.id.setup_role)
        TVphone = findViewById(R.id.setup_phone)

        databaseReference.keepSynced(true)
        welcome()
        addValueEventListener()
    }

    private fun welcome() {
        if (mUser != null) {
            name = mUser!!.displayName
            phone = mUser!!.phoneNumber
        } else sendToAuth()
    }

    private fun addValueEventListener() {
        databaseReference.child(phone!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user =
                    dataSnapshot.getValue(
                        User::class.java
                    )
                if (user != null) {
                    if (user.name != null && user.role != null) {
                        setupName.setText(user.name)
                        TVphone.text = phone
                        TVrole.text = user.role
                        updateUI(user.role)
                    } else {
                        setupName.setHint(R.string.user)
                        TVphone.text = phone
                        TVrole.text = getString(R.string.user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Error Initial:$databaseError")
                Toast.makeText(this@SetupActivity, "User Not Found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(role: String?) {
        if (role != "ADMIN") updateuserbtn.visibility =
            View.GONE else updateuserbtn.visibility = View.VISIBLE
    }

    override fun onClick(view: View) {
        if (view.id == R.id.setup_submit_btn) firebaseDataUpdate() else if (view.id == R.id.setup_user_update_btn) displayAlert()
    }

    private fun firebaseDataUpdate() {
        if (validate()) {
            val mreference = FirebaseDatabase.getInstance().getReference("Users")
            val fh = User(name, phone, role)
            mreference.child(phone!!).setValue(fh)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SetupActivity,
                            R.string.data_added, Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.i(TAG, getString(R.string.data_added))
                        sendToMain()
                    } else {
                        Toast.makeText(
                            this@SetupActivity,
                            R.string.data_not_added,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i(
                            TAG,
                            getString(R.string.data_not_added) + task.exception!!.message
                        )
                    }
                }
        } else Toast.makeText(
            this,
            R.string.add_required, Toast.LENGTH_SHORT
        ).show()
    }

    private fun gatherData() {
        name = setupName.text.toString().trim { it <= ' ' }
        role = TVrole.text.toString().trim { it <= ' ' }
    }

    private fun validate(): Boolean {
        gatherData()
        if (role!!.isEmpty()) role = "USER"
        return TextUtils.isEmpty(Sname)
    }

    private fun displayAlert() {
        try {
            val build =
                AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialogview_user, null)
            build.setView(dialogView)
            val phoneno = dialogView.findViewById<EditText>(R.id.dvu_phno)
            val role = dialogView.findViewById<Spinner>(R.id.dvu_role)
            val list: MutableList<String> =
                ArrayList()
            list.add("USER")
            list.add("ADMIN")
            val dataAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            role.adapter = dataAdapter
            build.setTitle("Update User Role")
            build.setPositiveButton(
                "UPDATE"
            ) { arg0: DialogInterface?, arg1: Int ->
                validateUserUpdate(
                    phoneno.text.toString().trim { it <= ' ' },
                    role.selectedItem.toString()
                )
            }
            build.setNegativeButton(
                "CANCEL"
            ) { dialog: DialogInterface, arg1: Int -> dialog.dismiss() }
            val alert = build.create()
            alert.show()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Alert Alert" + e.message)
        }
    }

    private fun validateUserUpdate(uph: String, ur: String) {
        if (TextUtils.isEmpty(uph)) Toast.makeText(
            this,
            R.string.add_required, Toast.LENGTH_SHORT
        )
            .show() else verifyUser(uph, ur)
    }

    private fun updateUser(uph: String, ur: String) {
        val mreference = FirebaseDatabase.getInstance().getReference("Users")
        mreference.child("+91$uph").child("role").setValue(ur)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.i(TAG, "User $uph Role Updated to $ur")
                    Toast.makeText(applicationContext, "Role Updated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.i(TAG, "User $uph Role Not Updated to $ur")
                    Toast.makeText(applicationContext, "Role Not Updated", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun verifyUser(uph: String, ur: String) {
        FirebaseDatabase.getInstance().getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("+91$uph")) updateUser(uph, ur) else Toast.makeText(
                        applicationContext,
                        "User Not Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Error: $databaseError")
                    Toast.makeText(
                        applicationContext,
                        "Error: $databaseError",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setup_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout_btn -> {
            logout()
            true
        }
        else -> false
    }

    private fun logout() {
        mAuth.signOut()
        if (mAuth.currentUser == null) sendToAuth()
    }

    private fun sendToAuth() {
        startActivity(Intent(this, PhoneAuthActivity::class.java))
        finish()
    }

    private fun sendToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}