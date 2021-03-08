package com.john.shopifyApplication.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.firebase.client.Firebase
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.common.APIManager
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.extensions.isNumeric
import com.john.shopifyApplication.fragment.*
import com.john.shopifyApplication.model.Image
import com.john.shopifyApplication.model.Product
import com.john.shopifyApplication.model.Variants
import com.john.shopifyApplication.utils.ImageHelper
import com.john.shopifyApplication.utils.MyToastUtil
import kotlinx.android.synthetic.main.activity_addproduct.*
import kotlinx.android.synthetic.main.activity_addproduct.closeBtn
import kotlinx.android.synthetic.main.activity_addproduct.imageView
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*


class AddProductActivity: BaseActivity() {

    companion object {
        const val TAG = Constants.BASICTAG + "AddProductActivity"
    }

    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var imageString = ""

    //Fragments
    private lateinit var currentFragment: BaseFragment
    private val defaultFragment: DefaultFragment = DefaultFragment.newInstance()
    private val braceletFragment: BraceletFragment = BraceletFragment.newInstance()
    private val ringFragment: RingFragment = RingFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addproduct)

        closeBtn.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            onSubmit()
        }

        imageView.setOnClickListener {
            showPictureDialog()
        }

        jewelryTypeSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("LongLogTag")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG,"jewelryTypeSpinner Position -> $p2")

                when(p2) {
                    0 -> replaceFragment(braceletFragment)
                    11 -> replaceFragment(ringFragment)
                    else -> replaceFragment(defaultFragment)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        replaceFragment()

    }

    private fun replaceFragment(selectedFragment: BaseFragment = braceletFragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerFL, selectedFragment)
            .commit()

        currentFragment = selectedFragment
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        if (hasNoPermissions()) {
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ACTIVITY_GALLERY_OK) {
            if (data != null) {

                val contentURI = data.data
                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
                    bitmap = ImageHelper.getResizedBitmap(bitmap,Constants.PHOTOIMAGERESIZE)
                    bitmap = ImageHelper.modificationGallery(bitmap,contentURI!!,this)
                    saveImage(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                saveImage(thumbnail)
            }
        }
    }

    @SuppressLint("LongLogTag")
    @TargetApi(Build.VERSION_CODES.O)
    private fun saveImage(bitmap: Bitmap) {
        val round = RoundedBitmapDrawableFactory.create(resources, bitmap)
        round.cornerRadius = HomeFragment.roundedCon
        this.imageView.setImageDrawable(round)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        imageString = Base64.getEncoder().encodeToString(b)
        Log.d(TAG, imageString)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constants.ACTIVITY_CAMERA_OK)
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Constants.ACTIVITY_GALLERY_OK)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun validation(): Boolean {

        if (refET.text.toString().isEmpty()) {
            MyToastUtil.showWarning(this,"Ref# field is required.")
            return false
        }

        if (!priceET.text.toString().isNumeric()) {
            MyToastUtil.showWarning(this,"Price field is numeric.")
            return false
        }

        return true
    }

    private fun onSubmit() {

        if(!validation()) {
            return
        }

        var price = priceET.text.toString()
        if(price.isNotEmpty()) {
            if(!price.contains("$")) price = "$$price"
        }

        val title = currentFragment.getTitle(jewelryTypeSpin.selectedItem.toString())
        val bodyHtml = currentFragment.getDescription(refET.text.toString())

        val newProduct = Product(0,title, Image(0,0,imageString), Variants(price),bodyHtml)
        val successCallback: (Long) -> Unit = { productID ->
            runOnUiThread {
                addProductToFirebase(productID)
            }
        }

        val errorCallback: (String) -> Unit = { _ ->
            runOnUiThread {
                MyToastUtil.showWarning(this, "Failed")
            }
        }

        APIManager.share.postProduct(newProduct, successCallback, errorCallback)

    }

    private fun addProductToFirebase(productID: Long) {

        val url = "${Constants.PRODUCTURL}.json"
        val shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val currentUser = shared.getString(Constants.PREF_CURRENT_USER,"")

        if(currentUser == null || currentUser.isEmpty()) {
            MyToastUtil.showWarning(this, "Item saved failed")
            return
        }

        showWaitDialog()
        val request = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { s ->
                closeWaitDialog()
                if (s == "null") {
                    success(productID.toString())
                } else {
                    try {
                        val obj = JSONObject(s)
                        if (obj.has(currentUser)) {

                            val oldProducts = obj.getString(currentUser)
                            success("$oldProducts,$productID")

                        } else {
                            success(productID.toString())
                        }
                    } catch (e: JSONException) {
                        MyToastUtil.showWarning(applicationContext, "Item saved failed")
                        e.printStackTrace()
                    }
                }
            }, Response.ErrorListener { volleyError ->
                closeWaitDialog()
                MyToastUtil.showWarning(applicationContext, "Item saved failed")
                println("" + volleyError)
        })

        val rQueue = Volley.newRequestQueue(this)
        rQueue.add(request)

    }

    private fun success(productID: String) {
        val shared: SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val currentUser = shared.getString(Constants.PREF_CURRENT_USER,"")
        val reference = Firebase(Constants.PRODUCTURL)
        reference.child(currentUser).setValue(productID)
        MyToastUtil.showMessage(this, "Submitted Successfully!")
        finish()
    }


}