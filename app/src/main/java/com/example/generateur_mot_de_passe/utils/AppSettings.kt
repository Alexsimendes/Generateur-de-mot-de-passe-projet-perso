package com.example.generateur_mot_de_passe.utils


import android.content.SharedPreferences
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.Générateur_de_mot_de_passe.R
import com.example.generateur_mot_de_passe.activity.MainApplication




class AppSettings private constructor() {

    var mRateDialogShowed: Boolean = false
    var mApplicationLaunchCount: Int = 0
    var mApplicationLastVersionCode: Int = 0

    var mAppTheme: AppTheme = AppTheme.DARK

    var mAutoClipboardClearing: Boolean = true

    var mPasswordUseLowerCase: Boolean = false
    var mPasswordUseUpperCase: Boolean = false
    var mPasswordUseNumbers: Boolean = false
    var mPasswordUseSymbols: Boolean = false
    var mPasswordSymbols: String? = null
    var mPasswordUseUniqueChars: Boolean = false
    var mPasswordUseSimilarChars: Boolean = false
    var mPasswordsCount: Int = 0
    var mPasswordLength: Int = 0

    private var mPreferences: SharedPreferences? = null

    val appThemeId: Int
        get() = if (mAppTheme === AppTheme.DARK) {
            R.style.AppBaseTheme
        }
        else {
            R.style.AppBaseThemeDark
        }

    init {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.appContext)
        setDefaultNonResetableSettings()
        setDefaultSettings()
        loadSettings()
    }

    private fun setDefaultNonResetableSettings() {
        mRateDialogShowed = false
        mApplicationLaunchCount = 0
        mApplicationLastVersionCode = 0
    }

    private fun setDefaultSettings() {
        mAppTheme = AppTheme.DARK
        mAutoClipboardClearing = true
        mPasswordUseLowerCase = true
        mPasswordUseUpperCase = true
        mPasswordUseNumbers = true
        mPasswordUseSymbols = false
        mPasswordSymbols = DEFAULT_PASSWORD_SYMBOLS
        mPasswordUseUniqueChars = false
        mPasswordUseSimilarChars = true
        mPasswordsCount = DEFAULT_PASSWORDS_COUNT
        mPasswordLength = DEFAULT_PASSWORD_LENGTH
    }

    fun getColor(id: Int): Int {
        var id = id
        val context = MainApplication.appContext
        val th = ContextThemeWrapper(context, appThemeId)
        val typedValue = TypedValue()
        th.theme.resolveAttribute(id, typedValue, true)
        if (typedValue.resourceId != 0) {
            id = typedValue.resourceId
        }
        return ContextCompat.getColor(context!!, id)
    }

    fun saveSettings() {
        val editor = mPreferences!!.edit()
        editor.putBoolean(SETTINGS_RATE_DIALOG_SHOWED, mRateDialogShowed)
        editor.putInt(SETTINGS_APPLICATION_LAUNCH_COUNT, mApplicationLaunchCount)
        editor.putInt(
            SETTINGS_APPLICATION_LAST_VERSION_CODE,
            Utils.getVersionCode(MainApplication.appContext!!)
        )
        editor.putInt(SETTINGS_APP_THEME, mAppTheme.ordinal)
        editor.putBoolean(SETTINGS_AUTO_CLIPBOARD_CLEARING, mAutoClipboardClearing)
        editor.putBoolean(SETTINGS_PASSWORD_USE_LOWER_CASE, mPasswordUseLowerCase)
        editor.putBoolean(SETTINGS_PASSWORD_USE_UPPER_CASE, mPasswordUseUpperCase)
        editor.putBoolean(SETTINGS_PASSWORD_USE_NUMBERS, mPasswordUseNumbers)
        editor.putBoolean(SETTINGS_PASSWORD_USE_SYMBOLS, mPasswordUseSymbols)
        editor.putString(SETTINGS_PASSWORD_SYMBOLS, mPasswordSymbols)
        editor.putBoolean(SETTINGS_PASSWORD_USE_UNIQUE_CHARS, mPasswordUseUniqueChars)
        editor.putBoolean(SETTINGS_PASSWORD_USE_SIMILAR_CHARS, mPasswordUseSimilarChars)
        editor.putInt(SETTINGS_PASSWORDS_COUNT, mPasswordsCount)
        editor.putInt(SETTINGS_PASSWORD_LENGTH, mPasswordLength)
        editor.apply()
    }

    private fun loadSettings() {
        fixOldSettings()
        mRateDialogShowed = mPreferences!!.getBoolean(SETTINGS_RATE_DIALOG_SHOWED, mRateDialogShowed)
        mApplicationLaunchCount = mPreferences!!.getInt(SETTINGS_APPLICATION_LAUNCH_COUNT, mApplicationLaunchCount)
        mApplicationLastVersionCode = mPreferences!!.getInt(SETTINGS_APPLICATION_LAST_VERSION_CODE, mApplicationLastVersionCode)
        mAppTheme = AppTheme.values()[mPreferences!!.getInt(SETTINGS_APP_THEME, mAppTheme.ordinal)]
        mAutoClipboardClearing = mPreferences!!.getBoolean(SETTINGS_AUTO_CLIPBOARD_CLEARING, mAutoClipboardClearing)
        mPasswordUseLowerCase = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_LOWER_CASE, mPasswordUseLowerCase)
        mPasswordUseUpperCase = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_UPPER_CASE, mPasswordUseUpperCase)
        mPasswordUseNumbers = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_NUMBERS, mPasswordUseNumbers)
        mPasswordUseSymbols = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_SYMBOLS, mPasswordUseSymbols)
        mPasswordSymbols = mPreferences!!.getString(SETTINGS_PASSWORD_SYMBOLS, mPasswordSymbols)
        mPasswordUseUniqueChars = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_UNIQUE_CHARS, mPasswordUseUniqueChars)
        mPasswordUseSimilarChars = mPreferences!!.getBoolean(SETTINGS_PASSWORD_USE_SIMILAR_CHARS, mPasswordUseSimilarChars)
        mPasswordsCount = mPreferences!!.getInt(SETTINGS_PASSWORDS_COUNT, mPasswordsCount)
        mPasswordLength = mPreferences!!.getInt(SETTINGS_PASSWORD_LENGTH, mPasswordLength)
    }

    private fun fixOldSettings() {
        val editor = mPreferences!!.edit()
        if (mPreferences!!.contains("SETTINGS_PASSWORD_LOWER_CASE")) {
            editor.putBoolean(SETTINGS_PASSWORD_USE_LOWER_CASE, mPreferences!!.getBoolean("SETTINGS_PASSWORD_LOWER_CASE", mPasswordUseLowerCase))
            editor.remove("SETTINGS_PASSWORD_LOWER_CASE")
        }
        if (mPreferences!!.contains("SETTINGS_PASSWORD_UPPER_CASE")) {
            editor.putBoolean(SETTINGS_PASSWORD_USE_UPPER_CASE, mPreferences!!.getBoolean("SETTINGS_PASSWORD_UPPER_CASE", mPasswordUseLowerCase))
            editor.remove("SETTINGS_PASSWORD_UPPER_CASE")
        }
        if (mPreferences!!.contains("SETTINGS_PASSWORD_NUMBERS")) {
            editor.putBoolean(SETTINGS_PASSWORD_USE_NUMBERS, mPreferences!!.getBoolean("SETTINGS_PASSWORD_NUMBERS", mPasswordUseLowerCase))
            editor.remove("SETTINGS_PASSWORD_NUMBERS")
        }
        if (mPreferences!!.contains("SETTINGS_PASSWORD_UNIQUE_CHARS")) {
            editor.putBoolean(SETTINGS_PASSWORD_USE_UNIQUE_CHARS, mPreferences!!.getBoolean("SETTINGS_PASSWORD_UNIQUE_CHARS", mPasswordUseLowerCase))
            editor.remove("SETTINGS_PASSWORD_UNIQUE_CHARS")
        }
        editor.apply()
    }

    companion object {
        const val MIN_PASSWORDS_COUNT = 1
        const val MAX_PASSWORDS_COUNT = 99
        const val MIN_PASSWORD_LENGTH = 1
        const val MAX_PASSWORD_LENGTH = 999
        const val DEFAULT_PASSWORDS_COUNT = 1
        const val DEFAULT_PASSWORD_LENGTH = 20
        const val DEFAULT_PASSWORD_SYMBOLS = "\'`\"!?,.:;$%&@~#()<>{}[]_*-+^=/|\\"
        const val PASSWORD_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz"
        const val PASSWORD_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val PASSWORD_NUMBERS = "0123456789"
        val SIMILAR_CHARS = arrayOf("0O", "1lI")
        const val CLIPBOARD_CLEAR_DELAY = 15000L
        const val CLIPBOARD_LABEL = "clip"

        private const val SETTINGS_RATE_DIALOG_SHOWED = "SETTINGS_RATE_DIALOG_SHOWED"
        private const val SETTINGS_APPLICATION_LAUNCH_COUNT = "SETTINGS_APPLICATION_LAUNCH_COUNT"
        private const val SETTINGS_APPLICATION_LAST_VERSION_CODE = "SETTINGS_APPLICATION_LAST_VERSION_CODE"
        private const val SETTINGS_APP_THEME = "SETTINGS_APP_THEME"
        private const val SETTINGS_AUTO_CLIPBOARD_CLEARING = "SETTINGS_AUTO_CLIPBOARD_CLEARING"
        private const val SETTINGS_PASSWORD_USE_LOWER_CASE = "SETTINGS_PASSWORD_USE_LOWER_CASE"
        private const val SETTINGS_PASSWORD_USE_UPPER_CASE = "SETTINGS_PASSWORD_USE_UPPER_CASE"
        private const val SETTINGS_PASSWORD_USE_NUMBERS = "SETTINGS_PASSWORD_USE_NUMBERS"
        private const val SETTINGS_PASSWORD_USE_SYMBOLS = "SETTINGS_PASSWORD_USE_SYMBOLS"
        private const val SETTINGS_PASSWORD_SYMBOLS = "SETTINGS_PASSWORD_SYMBOLS"
        private const val SETTINGS_PASSWORD_USE_UNIQUE_CHARS = "SETTINGS_PASSWORD_USE_UNIQUE_CHARS"
        private const val SETTINGS_PASSWORD_USE_SIMILAR_CHARS = "SETTINGS_PASSWORD_USE_SIMILAR_CHARS"
        private const val SETTINGS_PASSWORDS_COUNT = "SETTINGS_PASSWORDS_COUNT"
        private const val SETTINGS_PASSWORD_LENGTH = "SETTINGS_PASSWORD_LENGTH"

        private var mInstance: AppSettings? = null

        private fun createInstance(): AppSettings {
            mInstance = AppSettings()
            return mInstance as AppSettings
        }

        fun destroyInstance() {
            mInstance = null
        }

        val instance: AppSettings
            get() = if (mInstance != null) {
                mInstance!!
            }
            else {
                createInstance()
            }
    }

}
