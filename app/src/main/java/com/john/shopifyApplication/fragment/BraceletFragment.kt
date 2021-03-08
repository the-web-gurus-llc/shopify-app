package com.john.shopifyApplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.john.shopifyApplication.R
import com.john.shopifyApplication.base.BaseActivity
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.extensions.init
import kotlinx.android.synthetic.main.fragment_bracelet.*
import kotlinx.android.synthetic.main.fragment_bracelet.view.*
import java.util.ArrayList

class BraceletFragment: BaseFragment() {

    companion object {

        const val TAG = Constants.BASICTAG + "BraceletFragment"

        @JvmStatic
        fun newInstance() =
            BraceletFragment().apply {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_bracelet, container, false)
        initSpinners(view)
        return view
    }

    override fun getTitle(jewelryType: String): String {

        var title = ""

        /*
        <<Total Carats for the piece>> Carats <<Style>> <<Jewelry Type>> <<Main Stone Shape>> <<Main Stone Type>> <<Main Stone Color>> <<Main Stone Clarity>> <<Metal>>
         */

        if(totalCaratsPieceET.text.toString().isNotEmpty()) {
            title += totalCaratsPieceET.text.toString() + " Carats "
        }

        title += braceletTypeSpin.selectedItem.toString()
        //title += getRealString(jewelryType)
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

        bodyHtml += "Bracelet Type : " + braceletTypeSpin.selectedItem.toString() + VALUE_ENTER +
                "Bracelet Gender : " + braceletGenderSpin.selectedItem.toString() + VALUE_ENTER +
                "Length in Inches : " + lengthInInchesSpin.selectedItem.toString() + VALUE_ENTER +
                "Width : " + widthEt.text.toString() + VALUE_ENTER +
                "Bracelet Resizable  : " + braceletResizableSpin.selectedItem.toString() + VALUE_ENTER +
                "Main Stone/s : " + mainStoneSpin.selectedItem .toString() + VALUE_ENTER +
                "Main Stone Creation/Treatment : " + mainStoneCreationSpin.selectedItem .toString() + VALUE_ENTER +
                "Main Stone Cut : " + mainStoneCutSpin.selectedItem .toString() + VALUE_ENTER +
                "MainStoneCutQuality : " + mainStoneCutQualitySpin.selectedItem .toString() + VALUE_ENTER +
                "Main Stone Carats : " + mainStoneCaratsEt.text .toString() + VALUE_ENTER +
                "Main Stone Size in mm : " + mainStoneSizeInMMEt.text .toString() + VALUE_ENTER +
                "Number of Main Stones : " + numberOfMainStonesEt.text .toString() + VALUE_ENTER +
                "Main Stone Color : " + mainStoneColorSpin.selectedItem .toString() + VALUE_ENTER +
                "Main Stone Clarity/Quality : " + mainStoneClaritySpin.selectedItem .toString() + VALUE_ENTER +
                "Appraisal Included : " + appraisalIncludeSpin.selectedItem .toString() + VALUE_ENTER +
                "Lab : " + labSpin.selectedItem .toString() + VALUE_ENTER +
                "Side Stones : " + sideStonesSpin.selectedItem .toString() + VALUE_ENTER +
                "Side Stone Creation/Treatment : " + sideStoneCreationSpin.selectedItem .toString() + VALUE_ENTER +
                "Side Stone Cut : " + sideStoneCutSpin.selectedItem .toString() + VALUE_ENTER +
                "Side Stone Carats : " + sideStoneCaratsEt.text .toString() + VALUE_ENTER +
                "Side Stone Color : " + sideStoneColorSpin.selectedItem .toString() + VALUE_ENTER +
                "Side Stone Clarity/Quality : " + sideStoneClaritySpin.selectedItem .toString() + VALUE_ENTER +
                "Total Carats  : " + totalCaratsPieceET.text .toString() + VALUE_ENTER +
                "Metal : " + metalSpin.selectedItem .toString() + VALUE_ENTER +
                "Metal Stamp : " + metalStampSpin.selectedItem .toString() + VALUE_ENTER +
                "Center PearlSize : " + centerPearlSizeSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryUniformity : " + jewelryUniformitySpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlLuster : " + jewelryPearlLusterSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlNacreThickness : " + jewelryPearlNacreThicknessSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlShape : " + jewelryPearlShapeSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlSurfaceMarkings : " + jewelryPearlSurfaceMarkingsSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlBodycolor : " + jewelryPearlBodyColorSpin.selectedItem .toString() + VALUE_ENTER +
                "JewelryPearlOvertone : " + jewelryPearlOvertoneSpin.selectedItem.toString() + VALUE_ENTER

        return bodyHtml
    }

    private fun initSpinners(view: View) {

        view.braceletTypeSpin.init((activity as BaseActivity), getStringArray("braceletTypeSpin"))
        view.braceletGenderSpin.init((activity as BaseActivity), getStringArray("braceletGenderSpin"))
        view.lengthInInchesSpin.init((activity as BaseActivity), getStringArray("lengthInInchesSpin"))
        view.braceletResizableSpin.init((activity as BaseActivity), getStringArray("braceletResizableSpin"))
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
        return super.getStringArray("bracelet/$assetsName")
    }

}