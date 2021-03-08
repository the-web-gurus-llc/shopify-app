package com.john.shopifyApplication.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.extensions.init
import com.john.shopifyApplication.main.AddProductActivity
import kotlinx.android.synthetic.main.fragment_ring.*
import kotlinx.android.synthetic.main.fragment_ring.view.*
import java.util.ArrayList

class RingFragment: BaseFragment() {

    companion object {
        const val TAG = Constants.BASICTAG + "RingFragment"

        @JvmStatic
        fun newInstance() =
            RingFragment().apply {}
    }

    private lateinit var myView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_ring, container, false)
        myView = view
        view.ringStyleSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("LongLogTag")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG,"ringStyleSpin Position -> $p2")

                when(p2) {
                    0 -> changeSubRingStyleSpin(true, "Baby")
                    1 -> changeSubRingStyleSpin(false, "Color Stone")
                    2 -> changeSubRingStyleSpin(false, "Engagement")
                    3 -> changeSubRingStyleSpin(false, "Engagement Set")
                    4 -> changeSubRingStyleSpin(true, "Eternity Band")
                    5 -> changeSubRingStyleSpin(false, "Fancy")
                    6 -> changeSubRingStyleSpin(false, "Men's Ring")
                    7 -> changeSubRingStyleSpin(true, "Semi-Mount")
                    8 -> changeSubRingStyleSpin(true, "Toe Rings")
                    9 -> changeSubRingStyleSpin(true, "Band")
                    10 -> changeSubRingStyleSpin(false, "Wedding Band Set")
                    else ->
                        Log.d(TAG, "subRingStyleSpin Error.")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        initSpinners(view)
        changeSubRingStyleSpin()

        return view
    }

    private fun changeSubRingStyleSpin(isHide: Boolean = true, name: String = "") {

        if(isHide) {
            myView.subRingStyleLL.visibility = View.GONE
            return
        }

        myView.subRingStyleLL.visibility = View.VISIBLE
        myView.subRingStyleTv.text = "$name :"
        var assetsName = name
        assetsName = assetsName.replace(" ", "")
        assetsName = assetsName.replace("\'", "")
        assetsName = assetsName.toLowerCase()
        myView.subRingStyleSpin.init((activity as BaseActivity), getStringArray_subRing(assetsName))

    }

    override fun getTitle(jewelryType: String): String {

        var title = ""

        /*
        <<Total Carats for the piece>> Carats <<Style>> <<Jewelry Type>> <<Main Stone Shape>> <<Main Stone Type>> <<Main Stone Color>> <<Main Stone Clarity>> <<Metal>>
         */

        if(totalCaratsEt.text.toString().isNotEmpty()) {
            title += totalCaratsEt.text.toString() + " Carats "
        }

        title += getRealString(jewelryType)
        title += getRealString(mainStoneSpin.selectedItem.toString())
        title += getRealString(mainStoneColorSpin.selectedItem.toString())
        title += getRealString(mainStoneClaritySpin.selectedItem.toString())
        title += getRealString(metalSpin.selectedItem.toString())

        return title
    }

    override fun getDescription(ref: String): String {
        var bodyHtml = ""

        bodyHtml += VALUE_ENTER
        bodyHtml += ref + VALUE_ENTER
        bodyHtml += VALUE_ENTER

        bodyHtml += "Ring Style : " + ringStyleSpin.selectedItem.toString() + VALUE_ENTER
        if(subRingStyleLL.visibility == View.VISIBLE) {
            bodyHtml += "${subRingStyleTv.text} : " + subRingStyleSpin.selectedItem.toString() + VALUE_ENTER
        }

        bodyHtml += "Face Overall Dimensions (In mm) : " + faceOverallDimensionsEt.text.toString() + VALUE_ENTER +
                "RingGender : " + ringGenderSpin.selectedItem.toString() + VALUE_ENTER +
                "Ring Size  : " + rignSizeSpin.selectedItem.toString() + VALUE_ENTER +
                "Ring Resizable : " + ringResizableSpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone/s : " + mainStoneSpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone Creation/Treatment : " + mainStoneCreationSpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone Cut : " + mainStoneCutSpin.selectedItem.toString() + VALUE_ENTER +
                "MainStoneCutQuality : " + mainStoneCutQualitySpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone Carats : " + mainStoneCaratsEt.text.toString() + VALUE_ENTER +
                "Main Stone mm : " + mainStoneMMEt.text.toString() + VALUE_ENTER +
                "Main Stone Color : " + mainStoneColorSpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone Clarity/Quality : " + mainStoneClaritySpin.selectedItem.toString() + VALUE_ENTER +
                "Appraisal Included : " + appraisalIncludeSpin.selectedItem.toString() + VALUE_ENTER +
                "Lab : " + labSpin.selectedItem.toString() + VALUE_ENTER +
                "Cert Number : " + certNumberEt.text.toString() + VALUE_ENTER +
                "Side Stones : " + sideStonesSpin.selectedItem.toString() + VALUE_ENTER +
                "Side Stone Creation/Treatment : " + sideStoneCreationSpin.selectedItem.toString() + VALUE_ENTER +
                "Side Stone Cut : " + sideStoneCutSpin.selectedItem.toString() + VALUE_ENTER +
                "Side Stone Carats : " + sideStoneCaratsEt.text.toString() + VALUE_ENTER +
                "Side Stone Color : " + sideStoneColorSpin.selectedItem.toString() + VALUE_ENTER +
                "Side Stone Clarity/Quality : " + sideStoneClaritySpin.selectedItem.toString() + VALUE_ENTER +
                "Total Carats  : " + totalCaratsEt.text.toString() + VALUE_ENTER +
                "Metal : " + metalSpin.selectedItem.toString() + VALUE_ENTER +
                "Metal Stamp : " + metalStampSpin.selectedItem.toString() + VALUE_ENTER +
                "Center PearlSize : " + centerPearlSizeSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryUniformity : " + jewelryUniformitySpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlLuster : " + jewelryPearlLusterSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlNacreThickness : " + jewelryPearlNacreThicknessSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlShape : " + jewelryPearlShapeSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlSurfaceMarkings : " + jewelryPearlSurfaceMarkingsSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlBodycolor : " + jewelryPearlBodyColorSpin.selectedItem.toString() + VALUE_ENTER +
                "JewelryPearlOvertone : " + jewelryPearlOvertoneSpin.selectedItem.toString() + VALUE_ENTER

        return bodyHtml
    }

    private fun initSpinners(view: View) {

        view.ringStyleSpin.init((activity as BaseActivity), getStringArray("ringStyleSpin"))
        view.ringGenderSpin.init((activity as BaseActivity), getStringArray("ringGenderSpin"))
        view.rignSizeSpin.init((activity as BaseActivity), getStringArray("rignSizeSpin"))
        view.ringResizableSpin.init((activity as BaseActivity), getStringArray("ringResizableSpin"))
        view.mainStoneSpin.init((activity as BaseActivity), getStringArray("mainStoneSpin"))
        view.mainStoneCreationSpin.init((activity as BaseActivity), getStringArray("mainStoneCreationSpin"))
        view.mainStoneCutSpin.init((activity as BaseActivity), getStringArray("mainStoneCutSpin"))
        view.mainStoneCutQualitySpin.init((activity as BaseActivity), getStringArray("mainStoneCutQualitySpin"))
        view.mainStoneColorSpin.init((activity as BaseActivity), getStringArray("mainStoneColorSpin"))
        view.mainStoneClaritySpin.init((activity as BaseActivity), getStringArray("mainStoneClaritySpin"))
        view.appraisalIncludeSpin.init((activity as BaseActivity), getStringArray("appraisalIncludeSpin"))
        view.labSpin.init((activity as BaseActivity), getStringArray("labSpin"))
        view.sideStonesSpin.init((activity as BaseActivity), getStringArray("sideStonesSpin"))
        view.sideStoneCreationSpin.init((activity as BaseActivity), getStringArray("sideStoneCreationSpin"))
        view.sideStoneCutSpin.init((activity as BaseActivity), getStringArray("sideStoneCutSpin"))
        view.sideStoneColorSpin.init((activity as BaseActivity), getStringArray("sideStoneColorSpin"))
        view.sideStoneClaritySpin.init((activity as BaseActivity), getStringArray("sideStoneClaritySpin"))
        view.metalSpin.init((activity as BaseActivity), getStringArray("metalSpin"))
        view.metalStampSpin.init((activity as BaseActivity), getStringArray("metalStampSpin"))
        view.centerPearlSizeSpin.init((activity as BaseActivity), getStringArray("centerPearlSizeSpin"))
        view.jewelryUniformitySpin.init((activity as BaseActivity), getStringArray("jewelryUniformitySpin"))
        view.jewelryPearlLusterSpin.init((activity as BaseActivity), getStringArray("jewelryPearlLusterSpin"))
        view.jewelryPearlNacreThicknessSpin.init((activity as BaseActivity), getStringArray("jewelryPearlNacreThicknessSpin"))
        view.jewelryPearlShapeSpin.init((activity as BaseActivity), getStringArray("jewelryPearlShapeSpin"))
        view.jewelryPearlSurfaceMarkingsSpin.init((activity as BaseActivity), getStringArray("jewelryPearlSurfaceMarkingsSpin"))
        view.jewelryPearlBodyColorSpin.init((activity as BaseActivity), getStringArray("jewelryPearlBodyColorSpin"))
        view.jewelryPearlOvertoneSpin.init((activity as BaseActivity), getStringArray("jewelryPearlOvertoneSpin"))

    }

    override fun getStringArray(assetsName: String): ArrayList<String> {
        return super.getStringArray("ring/$assetsName")
    }

    fun getStringArray_subRing(assetsName: String): ArrayList<String> {
        return super.getStringArray("ring/ringstyle/$assetsName")
    }

}