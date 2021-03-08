package com.john.shopifyApplication.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.Html
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.fragment.HomeFragment
import com.john.shopifyApplication.model.Product
import com.john.shopifyApplication.utils.ImageHelper
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity: BaseActivity() {

    companion object {
        const val TAG = Constants.BASICTAG + "DetailActivity"
        var product: Product? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initProduct()

        closeBtn.setOnClickListener {
            finish()
        }
    }

    fun initProduct() {
        if(product != null) {
            titleTV.text = product!!.title
            var price = product!!.variant.price
            if(!price.isEmpty() && !price.contains("$"))
                price = "$$price"
            priceTV.text = price

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descriptionTV.text = Html.fromHtml(product!!.body_html, Html.FROM_HTML_MODE_COMPACT)
            } else {
                descriptionTV.text = Html.fromHtml(product!!.body_html)
            }

            if (true) {
                soldTV.visibility = View.GONE
            } else soldTV.visibility = View.VISIBLE

            Glide.with(this)
                .asBitmap()
                .load(product!!.image.src)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val sqRes = ImageHelper.getSquredBitmap(resource)
                        val round = RoundedBitmapDrawableFactory.create(resources, sqRes)
                        round.cornerRadius = HomeFragment.roundedCon
                        imageView.setImageDrawable(round)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

}