package com.example.generateur_mot_de_passe.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.example.Générateur_de_mot_de_passe.R
import com.example.generateur_mot_de_passe.utils.AppSettings
import com.example.generateur_mot_de_passe.utils.Utils

class AboutActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(AppSettings.instance.appThemeId)
        setContentView(R.layout.activity_about)

        val textViewWebsiteValue = findViewById<View>(R.id.textViewWebsiteValue) as TextView
        textViewWebsiteValue.setOnClickListener{ Utils.openInternetBrowser(this@AboutActivity, getString(R.string.other_website), 0) }

        val textViewPrivacyPolicyValue = findViewById<View>(R.id.textViewPrivacyPolicyValue) as TextView
        textViewPrivacyPolicyValue.setOnClickListener { Utils.openInternetBrowser(this@AboutActivity, getString(R.string.other_privacy_policy_website), 0) }

        val textViewEmailValue = findViewById<View>(R.id.textViewEmailValue) as TextView
        textViewEmailValue.setOnClickListener { Utils.openEmailApplication(this@AboutActivity, getString(R.string.other_contact_email), 0) }

        val textViewSourceCodeValue = findViewById<View>(R.id.textViewSourceCodeValue) as TextView
        textViewSourceCodeValue.setOnClickListener { Utils.openInternetBrowser(this@AboutActivity, getString(R.string.other_source_code_repository), 0) }

        val textViewVersionValue = findViewById<View>(R.id.textViewVersionValue) as TextView
        textViewVersionValue.text = Utils.getVersionName(this)

        val textViewBuildNumberValue = findViewById<View>(R.id.textViewBuildNumberValue) as TextView

        val buildTime = Utils.getBuildTime(this)

        val versionCode = Utils.getVersionCode(this)

        if (versionCode > -1) {
            textViewBuildNumberValue.text = versionCode.toString() + if (buildTime != "") " ($buildTime)" else ""
        }
        else {
            textViewBuildNumberValue.text = getString(R.string.other_not_available)
        }
    }

}
