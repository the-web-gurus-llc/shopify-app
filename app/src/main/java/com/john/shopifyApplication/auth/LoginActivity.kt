package com.john.shopifyApplication.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.john.shopifyApplication.R
import com.john.shopifyApplication.auth.fingerprint.FingerprintActivity
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.extensions.isValidEmail
import com.john.shopifyApplication.main.MainActivity
import com.john.shopifyApplication.utils.MyToastUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread

class LoginActivity : BaseActivity() {
    companion object {
        const val TAG = Constants.BASICTAG + "LoginActivity"
    }

    private lateinit var userEmailStr: String
    private lateinit var passwordStr: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            SignUpNewUser()
        }

        btn_signin.setOnClickListener {
            SignIn()
        }

        btn_signin_touchID.setOnClickListener {
            SignInWithTouchID()
        }
    }

    private fun SignInWithTouchID() {

        var shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val registerUser = shared.getString(Constants.PREF_REGISTER_USER,"")
        if(registerUser.isEmpty()) {
            MyToastUtil.showWarning(this, "Please sign in or sign up to enable this function.")
            return
        }

        val intent = Intent(this, FingerprintActivity::class.java)
        startActivityForResult(intent, Constants.ACTIVITY_FINGERPRINT_OK)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_FINGERPRINT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }

    private fun SignIn() {
        if(checkValidation()) {
            showWaitDialog()

            var url: String

            if(userEmailStr.contains("@")) {
                url = "${Constants.USERURL}.json"
                userEmailStr = userEmailStr.replace(".", "dot@dot@dot")

//                auth.signInWithEmailAndPassword(userEmailStr, passwordStr)
//                    .addOnCompleteListener(this) { task ->
//                        closeWaitDialog()
//                        if (task.isSuccessful) {
//                            gotoMainScreen()
//                        } else {
//                            MyToastUtil.showWarning(this, getString(R.string.incorrectUserNameandPassword))
//                        }
//                    }
            }
            else {
                url = "${Constants.PHONENUMBERURL}.json"
            }

            val request = StringRequest(Request.Method.GET, url,
                Response.Listener { s ->
                    closeWaitDialog()
                    if (s == "null") {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_LONG).show()
                    } else {
                        try {
                            val obj = JSONObject(s)

                            if (!obj.has(userEmailStr)) {
                                MyToastUtil.showWarning(this@LoginActivity, "User not found")
                            } else if (obj.getJSONObject(userEmailStr).getString(Constants.PREF_PASSWORD) == passwordStr) {
                                onLoginSuccess(obj.getJSONObject(userEmailStr).getString(Constants.PREF_UUID))
                            } else {
                                MyToastUtil.showWarning(this@LoginActivity, "Incorrect password")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }, Response.ErrorListener { volleyError ->
                    closeWaitDialog()
                    println("" + volleyError)
                })

            val rQueue = Volley.newRequestQueue(this@LoginActivity)
            rQueue.add(request)

        }
    }

    fun onLoginSuccess(uuid: String) {
        var shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        shared.edit().putString(Constants.PREF_LOGIN, "true").apply()
        shared.edit().putString(Constants.PREF_REGISTER_USER, userEmailStr).apply()
        shared.edit().putString(Constants.PREF_REGISTER_PASSWORD, passwordStr).apply()
        shared.edit().putString(Constants.PREF_CURRENT_USER, uuid).apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun SignUpNewUser() {
        val registerActivity = Intent(this, RegisterActivity::class.java)
        startActivity(registerActivity)
        finish()
    }

    private fun checkValidation(): Boolean {
        userEmailStr = emailET.text.toString()
        passwordStr = passwordET.text.toString()

        if (userEmailStr.isEmpty()) {
            MyToastUtil.showWarning(this, "Email or Phone Number required")
            return false
        }

        if (!userEmailStr.contains("@")) {
            if(!userEmailStr.contains("+")) {
                MyToastUtil.showWarning(this, "Phone Number invalid. (ex: +14844732475)")
                return false
            }
        }

        if (passwordStr.isEmpty()) {
            MyToastUtil.showWarning(this, "Password required")
            return false
        }

        return true
    }
}
