package com.john.shopifyApplication.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.john.shopifyApplication.R
import com.john.shopifyApplication.auth.LoginActivity
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        const val TAG = Constants.BASICTAG + "MainActivity"
    }

    val homeFragment: HomeFragment = HomeFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragment()

        logOutBtn.setOnClickListener {
            onSignOut()
        }

        refreshBtn.setOnClickListener {
            homeFragment.LoadWithIds()
        }
    }

    private fun initFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerFL, homeFragment)
            .commit()
    }

    private fun onSignOut() {

        var shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        shared.edit().putString(Constants.PREF_LOGIN, "false").apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
