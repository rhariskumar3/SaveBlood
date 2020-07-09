package com.harishsk.saveblood.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.harishsk.saveblood.R
import com.harishsk.saveblood.utils.BottomNavigationBehavior

class MainActivity : AppCompatActivity() {
    var doubleBackToExitPressedOnce = false
    var transaction: FragmentTransaction? = null
    private lateinit var navigation: BottomNavigationView
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private var uid: String? = ""
    private var mreference: DatabaseReference? = null
    private var mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportActionBar
        loadfragment(HomeFragment())
        val layoutParams =
            navigation.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomNavigationBehavior()

        //Firbase
        initfirebase()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) sendToLogin() else uid = mAuth.currentUser!!.phoneNumber
        FirebaseDatabase.getInstance().getReference("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.hasChild(uid!!)) {
                        startActivity(Intent(this@MainActivity, SetupActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "Error: $databaseError")
                    Toast.makeText(this@MainActivity, "Error: $databaseError", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun initfirebase() {
        FirebaseApp.initializeApp(this)
        mreference = FirebaseDatabase.getInstance().reference
        mreference!!.keepSynced(true)
    }

    fun loadfragment(fragment: Fragment?) {
        transaction = supportFragmentManager.beginTransaction()
        transaction!!.replace(R.id.main_container, fragment!!)
        transaction!!.addToBackStack(null)
        transaction!!.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings_btn -> {
                startActivity(Intent(this@MainActivity, SetupActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun sendToLogin() {
        startActivity(Intent(this@MainActivity, PhoneAuthActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            R.string.click_back_again, Toast.LENGTH_SHORT
        ).show()
        Log.i(
            TAG, getString(
                R.string.click_back_again
            )
        )
        Handler()
            .postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    init {
        mOnNavigationItemSelectedListener =
            label@ BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        loadfragment(HomeFragment())
                        return@label true
                    }
                    R.id.navigation_add -> {
                        loadfragment(BloodGroupAdd())
                        return@label true
                    }
                    R.id.navigation_view -> {
                        loadfragment(BloodGroupView())
                        return@label true
                    }
                }
                false
            }
    }
}