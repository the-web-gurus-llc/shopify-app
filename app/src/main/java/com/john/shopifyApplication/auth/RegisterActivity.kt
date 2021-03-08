package com.john.shopifyApplication.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.extensions.isValidEmail
import com.john.shopifyApplication.utils.MyToastUtil
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject


class RegisterActivity : BaseActivity() {

    companion object {
        const val TAG = Constants.BASICTAG + "RegisterActivity"
    }

    private var email: String? = null
    private var phoneNumber: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_signin.setOnClickListener {
            SignInAgain()
        }

        btn_signup.setOnClickListener {
            SignUp()
        }
    }

    private fun SignUp() {

        if (isValid()) {

            val url = "${Constants.USERURL}.json"
            email = email!!.replace(".", "dot@dot@dot")

            showWaitDialog()
            val request = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
                override fun onResponse(s: String) {

                    if (s == "null") {
                        closeWaitDialog()
                        gotoPhoneVerifyScreen()
                    } else {
                        try {
                            val obj = JSONObject(s)

                            if (obj.has(email)) {
                                closeWaitDialog()
                                MyToastUtil.showWarning(this@RegisterActivity, "The email already exists")
                            } else {
                                continueWithPhoneNumber()
                            }
                        } catch (e: JSONException) {
                            closeWaitDialog()
                            e.printStackTrace()
                        }
                    }
                }

            }, object : Response.ErrorListener {
                override fun onErrorResponse(volleyError: VolleyError) {
                    closeWaitDialog()
                    println("" + volleyError)
                }
            })

            val rQueue = Volley.newRequestQueue(this@RegisterActivity)
            rQueue.add(request)
        }
    }

    fun continueWithPhoneNumber() {
        val url = "${Constants.PHONENUMBERURL}.json"
        val request = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(s: String) {
                closeWaitDialog()
                if (s == "null") {
                    MyToastUtil.showWarning(this@RegisterActivity, "Server side error.")
                } else {
                    try {
                        val obj = JSONObject(s)

                        if(obj.has(phoneNumber)) {
                            MyToastUtil.showWarning(this@RegisterActivity, "The phone number already exists")
                        } else {
                            gotoPhoneVerifyScreen()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

        }, object : Response.ErrorListener {
            override fun onErrorResponse(volleyError: VolleyError) {
                closeWaitDialog()
                println("" + volleyError)
            }
        })

        val rQueue = Volley.newRequestQueue(this@RegisterActivity)
        rQueue.add(request)
    }

    fun gotoPhoneVerifyScreen() {
        val intent = Intent(this, PhoneNumberVerifyActivity::class.java)
        intent.putExtra(Constants.PREF_EMAIL,email)
        intent.putExtra(Constants.PREF_PHONENUMBER,phoneNumber)
        intent.putExtra(Constants.PREF_PASSWORD,password)
        startActivityForResult(intent, Constants.ACTIVITY_PHONENUMBER_OK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_PHONENUMBER_OK) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }

    private fun isValid(): Boolean {

        if (emailETR.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this, "User email is required")
            return false
        }

        if (!emailETR.text.toString().isValidEmail()) {
            MyToastUtil.showWarning(this, "Invalid email")
            return false
        }

        if (phoneETR.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this, "Phone number is required")
            return false
        }

        if (passwordETR.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this, "Password is required")
            return false
        }

        if (passwordETR.text.toString().length < 6) {
            MyToastUtil.showWarning(this, "Password should be at least 6 characters")
            return false
        }

        if (confirmPasswordETR.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this, "Confirm password is required")
            return false
        }

        if (!confirmPasswordETR.text.toString().equals(passwordETR.text.toString())) {
            MyToastUtil.showWarning(applicationContext, "The password does not match")
            return false
        }

        email = emailETR.text.toString()
        phoneNumber = ccp.selectedCountryCodeWithPlus + phoneETR.text.toString()
        password = passwordETR.text.toString()

        return true
    }

    private fun SignInAgain() {
        val registerActivity = Intent(this, LoginActivity::class.java)
        startActivity(registerActivity)
        finish()
    }

}