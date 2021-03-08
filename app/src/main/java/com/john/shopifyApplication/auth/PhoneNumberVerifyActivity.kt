package com.john.shopifyApplication.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.utils.MyToastUtil
import kotlinx.android.synthetic.main.activity_phonenumberverify.*
import java.util.concurrent.TimeUnit
import com.google.android.gms.tasks.OnCompleteListener
import com.john.shopifyApplication.main.MainActivity
import com.firebase.client.Firebase
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class PhoneNumberVerifyActivity : BaseActivity() {
    companion object {
        const val TAG = Constants.BASICTAG + "PhoneNumberVerifyActivity"
    }


    private lateinit var userEmailStr: String
    private lateinit var phoneNumberStr: String
    private lateinit var passwordStr: String

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String? = null
    private var verificationCode: String? = null

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phonenumberverify)

        if (intent.extras != null) {
            userEmailStr = intent.getStringExtra(Constants.PREF_EMAIL)
            phoneNumberStr = intent.getStringExtra(Constants.PREF_PHONENUMBER)
            passwordStr = intent.getStringExtra(Constants.PREF_PASSWORD)
        }

        auth = FirebaseAuth.getInstance()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @SuppressLint("LongLogTag")
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)
                //MyToastUtil.showNotice(applicationContext,"Verified Successfully!")
            }

            @SuppressLint("LongLogTag")
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            @SuppressLint("LongLogTag")
            override fun onCodeSent(
                verificationid: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = verificationid
                resendToken = token
            }
        }

        resendVerifyCode.setOnClickListener {
            resendCode()
        }

        btn_confirm.setOnClickListener {
            confirmCode()
        }

        verifycodeSend()

    }

    fun verifycodeSend() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumberStr, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks)
    }

    private fun resendCode() {
        if(resendToken == null) {
            MyToastUtil.showWarning(this,"Try again!")
            return
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumberStr,        // Phone number to verify
            30,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            callbacks,         // OnVerificationStateChangedCallbacks
            resendToken)             // ForceResendingToken from callbacks
    }

    private fun confirmCode() {

        if(isValid()) {

            if(verificationId == null || verificationCode == null) {
                MyToastUtil.showWarning(this,"Try again")
                return
            }

            val credential = PhoneAuthProvider.getCredential(verificationId!!, verificationCode!!)
            signInWithPhoneAuthCredential(credential)
        }
    }

    @SuppressLint("LongLogTag")
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        showWaitDialog()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                closeWaitDialog()
                if (task.isSuccessful) {
                    MyToastUtil.showNotice(applicationContext,"Verified Successfully!")
                    createNewUser()
                } else {
                    Log.d(TAG, "signInWithCredential:failure " + task.exception!!)
                    MyToastUtil.showWarning(this, "Verification failed")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            })
    }

    @SuppressLint("LongLogTag")
    fun createNewUser() {
        auth.signOut()

        val uuid = UUID.randomUUID().toString()
        val reference = Firebase(Constants.USERURL)
        reference.child(userEmailStr).child(Constants.PREF_PASSWORD).setValue(passwordStr)
        reference.child(userEmailStr).child(Constants.PREF_UUID).setValue(uuid)
        //reference.child(userEmailStr).child(Constants.PREF_PHONENUMBER).setValue(phoneNumberStr)
        val reference_phone = Firebase(Constants.PHONENUMBERURL)
        reference_phone.child(phoneNumberStr).child(Constants.PREF_PASSWORD).setValue(passwordStr)
        reference_phone.child(phoneNumberStr).child(Constants.PREF_UUID).setValue(uuid)
        //reference_phone.child(phoneNumberStr).child(Constants.PREF_EMAIL).setValue(userEmailStr)
        MyToastUtil.showMessage(applicationContext,"Register Successfully!")

        var shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        shared.edit().putString(Constants.PREF_LOGIN, "true").apply()
        shared.edit().putString(Constants.PREF_REGISTER_USER, userEmailStr).apply()
        shared.edit().putString(Constants.PREF_REGISTER_PASSWORD, passwordStr).apply()
        shared.edit().putString(Constants.PREF_CURRENT_USER, uuid).apply()

        setResult(Activity.RESULT_OK)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

//        auth.createUserWithEmailAndPassword(userEmailStr, passwordStr)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//
//                    if( user != null) {
//
//                        val profileUpdates = UserProfileChangeRequest.Builder()
//                            .setPhotoUri(Uri.parse(passwordStr.getBase64String()))
//                            .setDisplayName(phoneNumberStr).build()
//
//                        user!!.updateProfile(profileUpdates)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Log.d(TAG, "User profile updated.")
//                                }
//                            }
//                    }
//
//                    setResult(Activity.RESULT_OK)
//                    MyToastUtil.showNotice(applicationContext,"Register Successfully!")
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    MyToastUtil.showWarning(this,"Couldn't create a new user. Try again.")
//                }
//            }
    }

    fun isValid(): Boolean{
        if (verifyCodeET.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this, "Verify code is required")
            return false
        }
        verificationCode = verifyCodeET.text.toString()
        return true
    }
}
