package com.john.shopifyApplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.john.shopifyApplication.R
import com.john.shopifyApplication.config.Constants
import kotlinx.android.synthetic.main.fragment_default.*

class DefaultFragment : BaseFragment() {

    companion object {
        const val TAG = Constants.BASICTAG + "DefaultFragment"

        @JvmStatic
        fun newInstance() =
            DefaultFragment().apply {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_default, container, false)
    }

    override fun getTitle(jewelryType: String): String {

        var title = ""

        /*
        <<Total Carats for the piece>> Carats <<Style>> <<Jewelry Type>> <<Main Stone Shape>> <<Main Stone Type>> <<Main Stone Color>> <<Main Stone Clarity>> <<Metal>>
         */

        if(totalCaratsPieceET.text.toString().isNotEmpty()) {
            title += totalCaratsPieceET.text.toString() + " Carats "
        }

        title += getRealString(jewelryType)
        title += getRealString(mainStoneShapeSpin.selectedItem.toString())
        title += getRealString(mainStoneTypeET.text.toString())
        title += getRealString(mainStoneColorET.text.toString())
        title += getRealString(mainStoneClarityET.text.toString())
        title += getRealString(metalSpin.selectedItem.toString())

        return title
    }

    override fun getDescription(ref: String): String {
        var bodyHtml = ""

        /*
        Stone Name : Natural <<Main Stone Type>>
        Stone Shape : <<Main Stone Shape>>
        Stone Details : There are 2 <<Main Stone Shape>> <<Main Stone Type>> approx. <<Weight of Center Stone>> carats average size. Natural earth mined stones.
        Color of Main Stone : <<Main Stone Color>>
        Clarity of Main Stone : <<Main Stone Clarity>>
        Total : Approx. <<Total Carats for the piece>> Carats

        EARRINGS SPECIFICATIONS
        Closure Type : <<Closure Type>>
         */

        bodyHtml += VALUE_ENTER
        bodyHtml += ref + VALUE_ENTER
        bodyHtml += VALUE_ENTER

        bodyHtml += "METAL SPECIFICATIONS$VALUE_ENTER"
        bodyHtml += metalSpin.selectedItem.toString() + VALUE_ENTER
        bodyHtml += VALUE_ENTER

        bodyHtml += "STONE SPECIFICATIONS$VALUE_ENTER"
        bodyHtml += "Stone Name : " + "Natural " + mainStoneTypeET.text.toString() + VALUE_ENTER
        bodyHtml += "Stone Shape : " + mainStoneShapeSpin.selectedItem.toString() + VALUE_ENTER
        bodyHtml += "Stone Details : " + "There are 2 ${mainStoneShapeSpin.selectedItem} ${mainStoneTypeET.text} approx. ${weightOfCenterStoneET.text} carats average size. Natural earth mined stones."
        bodyHtml += VALUE_ENTER
        bodyHtml += "Color of Main Stone : ${mainStoneColorET.text}" + VALUE_ENTER
        bodyHtml += "Clarity of Main Stone : ${mainStoneClarityET.text}" + VALUE_ENTER
        bodyHtml += "Total : Approx. ${totalCaratsPieceET.text} Carats" + VALUE_ENTER

        return bodyHtml
    }
}