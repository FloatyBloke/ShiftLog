package com.flangenet.shiftlog.Controller

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.flangenet.shiftlog.R
import com.flangenet.shiftlog.Utilities.EXTRA_EDIT_SHIFT
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAdView : AdView




    companion object {
        lateinit var instance: MainActivity
        private set
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        instance = this
/*        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                .build()
        )*/

        mAdView = findViewById(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        btnNewShift.setOnClickListener{openNewShift()}
        btnSettings.setOnClickListener{openSettings()}
        btnViewShifts.setOnClickListener{openViewShifts()}
        fabAbout.setOnClickListener{openAbout()}


        // Routine to add Tips column to database if it does not exist
        /*var db = DBHelper(this)

        if (db.isColumnExists("ShiftList","Tips")){
            Toast.makeText(this,"Tips Column Exists" , Toast.LENGTH_LONG).show()
        } else {

            Toast.makeText(this,"Tips Column Does Not Exist : " , Toast.LENGTH_LONG).show()

        }

*/



    }

    private fun openSettings(){
        val settingsIntent = Intent(this, Settings::class.java)
        startActivity(settingsIntent)
    }

    private fun openNewShift(){
        val newShiftIntent = Intent(this, EditShift::class.java)
        newShiftIntent.putExtra(EXTRA_EDIT_SHIFT,0)
        startActivity(newShiftIntent)
    }

    private fun openViewShifts(){
        val listShiftsIntent = Intent(this, ListShifts::class.java)
        startActivity(listShiftsIntent)
    }

    private fun openAbout() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.about_dialog)
        val versionName = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        val txtExtra= dialog.findViewById<TextView>(R.id.txtAboutDescription)
        val clAbout= dialog.findViewById(R.id.clAbout) as ConstraintLayout
        clAbout.setOnClickListener{ dialog.dismiss() }
        txtExtra.text =
            "${txtExtra.text}${System.lineSeparator()}${getString(R.string.about_version)} : ${versionName}"

        txtExtra.setOnClickListener{ dialog.dismiss() }
/*        val btnOk = dialog.findViewById(R.id.btnOk) as Button
        btnOk.setOnClickListener { dialog.dismiss() }*/


        dialog.show()

    }
}
