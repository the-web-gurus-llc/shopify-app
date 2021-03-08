package com.john.shopifyApplication.auth.fingerprint

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.main.MainActivity
import com.john.shopifyApplication.utils.MyToastUtil

@TargetApi(Build.VERSION_CODES.M)
class FingerprintHandler(private val context: Context) : FingerprintManager.AuthenticationCallback() {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private var cancellationSignal: CancellationSignal? = null

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {

        cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override//onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//
    fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        MyToastUtil.showWarning(context, "Authentication error\n$errString")
    }

    override//onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//
    fun onAuthenticationFailed() {
        MyToastUtil.showWarning(context, "Authentication failed")
    }

    override//onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        MyToastUtil.showNotice(context, "Authentication help\n$helpString")
    }

    override//onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    fun onAuthenticationSucceeded(
        result: FingerprintManager.AuthenticationResult
    ) {
        //MyToastUtil.showMessage(context, "Success!")
        val activity = context as FingerprintActivity
        activity.setResult(Activity.RESULT_OK)

        var shared: SharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        shared.edit().putString(Constants.PREF_LOGIN, "true").apply()
        shared.edit().putString(Constants.PREF_REGISTER_USER, "touchID").apply()
        shared.edit().putString(Constants.PREF_REGISTER_PASSWORD, "touchID").apply()

        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

}