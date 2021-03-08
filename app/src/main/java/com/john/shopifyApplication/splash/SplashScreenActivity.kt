package com.john.shopifyApplication.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.john.shopifyApplication.R
import com.john.shopifyApplication.auth.LoginActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.main.MainActivity
import com.john.shopifyApplication.utils.PreferenceUtils

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        const val TAG = Constants.BASICTAG + "SplashScreenActivity"
    }

    private var mDelayHandler: Handler? = null
    private val mDelay: Long = 1000

    private val mRunnable: Runnable = Runnable {

        var shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val userLogin = shared.getString(Constants.PREF_LOGIN,"")

        if( userLogin == "true") {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        PreferenceUtils.init(applicationContext)

        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, mDelay)
    }

    override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
}