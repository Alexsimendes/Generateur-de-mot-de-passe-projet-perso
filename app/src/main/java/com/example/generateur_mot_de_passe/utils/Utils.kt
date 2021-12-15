package com.example.generateur_mot_de_passe.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat
import android.util.DisplayMetrics
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import com.example.Générateur_de_mot_de_passe.R
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.math.roundToInt


object Utils {

    fun getDisplayMetrics(activity: AppCompatActivity): DisplayMetrics {
        val displaymetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displaymetrics)
        return displaymetrics
    }

    fun getDisplaySize(activity: AppCompatActivity): Point {
        val displayMetrics = getDisplayMetrics(activity)

        val size = Point()
        size.x = displayMetrics.widthPixels
        size.y = displayMetrics.heightPixels

        return size
    }

    @JvmOverloads
    fun dpToPx(displayMetrics: DisplayMetrics, dp: Int, xdpi: Boolean = true): Int {
        val dpi = if (xdpi) displayMetrics.xdpi else displayMetrics.ydpi
        return (dp * (dpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    @JvmOverloads
    fun pxToDp(displayMetrics: DisplayMetrics, px: Int, xdpi: Boolean = true): Int {
        val dpi = if (xdpi) displayMetrics.xdpi else displayMetrics.ydpi
        return (px / (dpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    fun getVersionName(context: Context): String {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }
        catch (e: Exception) {
            return context.getString(R.string.other_not_available)
        }
    }

    fun getVersionCode(context: Context): Int {
        try {
            return PackageInfoCompat.getLongVersionCode(context.packageManager.getPackageInfo(context.packageName, 0)).toInt()
        }
        catch (e: Exception) {
            return -1
        }
    }

    fun getBuildTime(context: Context): String {
        return formatTime(context, Date(), false, false)
    }


    fun openPlayStore(context: Context, playStorePackage: String, flags: Int) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$playStorePackage"))
            intent.flags = flags
            context.startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$playStorePackage"))
                intent.flags = flags
                context.startActivity(intent)
            }
            catch (ee: ActivityNotFoundException) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.dialog_no_internet_browser)
                builder.setPositiveButton(context.getString(R.string.dialog_button_ok), null)
                builder.show()
            }
        }
    }

    fun openInternetBrowser(context: Context, link: String, flags: Int) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.flags = flags
            context.startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.dialog_no_internet_browser)
            builder.setPositiveButton(context.getString(R.string.dialog_button_ok), null)
            builder.show()
        }
    }

    fun openEmailApplication(context: Context, link: String, flags: Int) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.flags = flags
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(link))
            val mailer = Intent.createChooser(intent, null)
            context.startActivity(mailer)
        }
        catch (e: ActivityNotFoundException) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.dialog_no_email_application)
            builder.setPositiveButton(context.getString(R.string.dialog_button_ok), null)
            builder.show()
        }
    }

    private fun getPattern(c: Context, locale: Locale, withTime: Boolean): String {
        var pattern = DateTimeFormat.patternForStyle("SM", locale)
        if (!withTime) {
            pattern = DateTimeFormat.patternForStyle("S-", locale)
        }

        if (!pattern.contains("yyyy")) {
            pattern = pattern.replace("yy", "yyyy")
        }

        if (DateFormat.is24HourFormat(c)) {
            pattern = pattern.replace("h", "H")
            if (pattern.contains("a")) {
                pattern = pattern.replace("a", "")
            }
        }
        else {
            pattern = pattern.replace("H", "h")
            if (!pattern.contains("a")) {
                val i = pattern.lastIndexOf("s")
                if (i > -1) {
                    pattern = pattern.substring(0, i + 1) + " aa " + pattern.substring(i + 1)
                }
            }
            else {
                pattern = pattern.replace("a", "aa")
            }
        }

        return pattern
    }

    private fun formatTime(c: Context, t: DateTime, withTime: Boolean, addUtcOffset: Boolean): String {
        var time: String

        val locale: Locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = c.resources.configuration.locales.get(0)
        }
        else {
            locale = c.resources.configuration.locale
        }

        val pattern = getPattern(c, locale, withTime)
        val format = DateTimeFormat.forPattern(pattern)

        val timeZone = DateTimeZone.UTC
        time = t.toDateTime(timeZone).toString(format)

        if (addUtcOffset) {
            val timeZoneOffsetMillis = timeZone.getOffset(t.millis).toLong()
            val hours = timeZoneOffsetMillis / (1000L * 60L * 60L)
            val minutes = Math.abs(timeZoneOffsetMillis / (1000L * 60L) % 60L)

            val hoursStr = (if (hours != 0L) if (hours >= 0) "+" else "-" else "±") + (if (hours <= 9 && hours >= -9) "0" else "") + Math.abs(hours)
            val minutesStr = (if (minutes <= 9) "0" else "") + minutes

            time += " (UTC$hoursStr:$minutesStr)"
        }

        return time
    }

    fun formatTime(c: Context, date: Date, withTime: Boolean, addUtcOffset: Boolean): String {
        val t = DateTime(date)
        return formatTime(c, t, withTime, addUtcOffset)
    }

    fun clamp(`val`: Int, min: Int, max: Int): Int {
        return Math.max(min, Math.min(max, `val`))
    }

}
