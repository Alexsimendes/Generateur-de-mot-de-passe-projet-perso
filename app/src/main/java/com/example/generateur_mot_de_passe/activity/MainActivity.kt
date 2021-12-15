package com.example.generateur_mot_de_passe.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.Générateur_de_mot_de_passe.BuildConfig
import com.example.Générateur_de_mot_de_passe.R
import com.example.generateur_mot_de_passe.utils.AppSettings
import com.example.generateur_mot_de_passe.utils.Utils
import java.security.SecureRandom
class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var mLinearLayoutPasswords: LinearLayout? = null
    private var mCheckBoxLowerCase: CheckBox? = null
    private var mCheckBoxUpperCase: CheckBox? = null
    private var mCheckBoxNumbers: CheckBox? = null
    private var mCheckBoxSymbols: CheckBox? = null
    private var mCheckBoxUniqueChars: CheckBox? = null
    private var mCheckBoxSimilarChars: CheckBox? = null

    private val mTextViewsStrength = ArrayList<TextView>()

    private var mEditTextSymbols: EditText? = null
    private var mEditTextPasswordsCount: EditText? = null
    private var mEditTextPasswordLength: EditText? = null
    private val mEditTextsPassword = ArrayList<EditText>()

    private val mProgressBarsStrength = ArrayList<ProgressBar>()

    private var mButtonPasswordsCountMinus: Button? = null
    private var mButtonPasswordsCountPlus: Button? = null
    private var mButtonPasswordLengthMinus: Button? = null
    private var mButtonPasswordLengthPlus: Button? = null
    private var mButtonGenerate: Button? = null
    private var mButtonCopy: Button? = null

    private var mViewPasswordMasterAd: View? = null

    private val mRandomSecure = SecureRandom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(AppSettings.instance.appThemeId)
        setContentView(R.layout.activity_main)

        AppSettings.instance.mApplicationLaunchCount++

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mLinearLayoutPasswords = findViewById(R.id.linearLayoutPasswords)

        mEditTextSymbols = findViewById(R.id.editTextSymbols)
        mEditTextPasswordsCount = findViewById(R.id.editTextPasswordsCount)
        mEditTextPasswordLength = findViewById(R.id.editTextPasswordLength)


        if (mEditTextSymbols != null) {
            mEditTextSymbols!!.isEnabled = AppSettings.instance.mPasswordUseSymbols
            mEditTextSymbols!!.setText(AppSettings.instance.mPasswordSymbols)
            mEditTextSymbols!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    AppSettings.instance.mPasswordSymbols = editable.toString().trim { it <= ' ' }
                }
            })
        }

        if (mEditTextPasswordsCount != null) {
            mEditTextPasswordsCount!!.setText(AppSettings.instance.mPasswordsCount.toString())
            mEditTextPasswordsCount!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    try {
                        AppSettings.instance.mPasswordsCount = Utils.clamp(Integer.valueOf(editable.toString()), AppSettings.MIN_PASSWORDS_COUNT, AppSettings.MAX_PASSWORDS_COUNT)
                    } catch (e: NumberFormatException) {
                        AppSettings.instance.mPasswordsCount = AppSettings.MIN_PASSWORDS_COUNT
                    }

                    updatePasswordLayouts(false)
                    generateAndShowPasswords()
                }
            })
        }

        if (mEditTextPasswordLength != null) {
            mEditTextPasswordLength!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    try {
                        AppSettings.instance.mPasswordLength = Utils.clamp(Integer.valueOf(editable.toString()), AppSettings.MIN_PASSWORD_LENGTH, AppSettings.MAX_PASSWORD_LENGTH)
                    }
                    catch (e: NumberFormatException) {
                        AppSettings.instance.mPasswordLength = AppSettings.MIN_PASSWORD_LENGTH
                    }

                }
            })
            mEditTextPasswordLength!!.setText(AppSettings.instance.mPasswordLength.toString())
        }

        mCheckBoxLowerCase = findViewById(R.id.checkBoxLowerCase)
        mCheckBoxUpperCase = findViewById(R.id.checkBoxUpperCase)
        mCheckBoxNumbers = findViewById(R.id.checkBoxNumbers)
        mCheckBoxSymbols = findViewById(R.id.checkBoxCustomSymbols)
        mCheckBoxUniqueChars = findViewById(R.id.checkBoxUniqueChars)
        mCheckBoxSimilarChars = findViewById(R.id.checkBoxSimilarChars)


        if (mCheckBoxLowerCase != null) {
            mCheckBoxLowerCase!!.setOnCheckedChangeListener { button, checked -> AppSettings.instance.mPasswordUseLowerCase = checked }
            mCheckBoxLowerCase!!.isChecked = AppSettings.instance.mPasswordUseLowerCase
        }

        if (mCheckBoxUpperCase != null) {
            mCheckBoxUpperCase!!.setOnCheckedChangeListener { button, checked -> AppSettings.instance.mPasswordUseUpperCase = checked }
            mCheckBoxUpperCase!!.isChecked = AppSettings.instance.mPasswordUseUpperCase
        }

        if (mCheckBoxNumbers != null) {
            mCheckBoxNumbers!!.setOnCheckedChangeListener { button, checked -> AppSettings.instance.mPasswordUseNumbers = checked }
            mCheckBoxNumbers!!.isChecked = AppSettings.instance.mPasswordUseNumbers
        }

        if (mCheckBoxSymbols != null) {
            mCheckBoxSymbols!!.setOnCheckedChangeListener { button, checked ->
                AppSettings.instance.mPasswordUseSymbols = checked
                mEditTextSymbols!!.isEnabled = checked
            }
            mCheckBoxSymbols!!.isChecked = AppSettings.instance.mPasswordUseSymbols
        }

        if (mCheckBoxUniqueChars != null) {
            mCheckBoxUniqueChars!!.setOnCheckedChangeListener { button, checked -> AppSettings.instance.mPasswordUseUniqueChars = checked }
            mCheckBoxUniqueChars!!.isChecked = AppSettings.instance.mPasswordUseUniqueChars
        }

        if (mCheckBoxSimilarChars != null) {
            mCheckBoxSimilarChars!!.setOnCheckedChangeListener { button, checked -> AppSettings.instance.mPasswordUseSimilarChars = checked }
            mCheckBoxSimilarChars!!.isChecked = AppSettings.instance.mPasswordUseSimilarChars
        }


        // Button
        mButtonPasswordsCountMinus = findViewById(R.id.buttonPasswordsCountMinus)
        mButtonPasswordsCountPlus = findViewById(R.id.buttonPasswordsCountPlus)
        mButtonPasswordLengthMinus = findViewById(R.id.buttonPasswordLengthMinus)
        mButtonPasswordLengthPlus = findViewById(R.id.buttonPasswordLengthPlus)
        mButtonGenerate = findViewById(R.id.buttonGenerate)
        mButtonCopy = findViewById(R.id.buttonCopy)

        if (mButtonPasswordsCountMinus != null) {
            mButtonPasswordsCountMinus!!.setOnClickListener {
                if (AppSettings.instance.mPasswordsCount > AppSettings.MIN_PASSWORDS_COUNT) {
                    mEditTextPasswordsCount!!.setText((AppSettings.instance.mPasswordsCount - 1).toString())
                }
            }
        }

        if (mButtonPasswordsCountPlus != null) {
            mButtonPasswordsCountPlus!!.setOnClickListener {
                if (AppSettings.instance.mPasswordsCount < AppSettings.MAX_PASSWORDS_COUNT) {
                    mEditTextPasswordsCount!!.setText((AppSettings.instance.mPasswordsCount + 1).toString())
                }
            }
        }

        if (mButtonPasswordLengthMinus != null) {
            mButtonPasswordLengthMinus!!.setOnClickListener {
                if (AppSettings.instance.mPasswordLength > AppSettings.MIN_PASSWORD_LENGTH) {
                    mEditTextPasswordLength!!.setText((AppSettings.instance.mPasswordLength - 1).toString())
                }
            }
        }

        if (mButtonPasswordLengthPlus != null) {
            mButtonPasswordLengthPlus!!.setOnClickListener {
                if (AppSettings.instance.mPasswordLength < AppSettings.MAX_PASSWORD_LENGTH) {
                    mEditTextPasswordLength!!.setText((AppSettings.instance.mPasswordLength + 1).toString())
                }
            }
        }

        if (mButtonGenerate != null) {
            mButtonGenerate!!.setOnClickListener { generateAndShowPasswords() }
        }

        if (mButtonCopy != null) {
            mButtonCopy!!.setOnClickListener {
                var passwords = ""
                val append = if (mEditTextsPassword.size > 1) "\n" else ""
                for (i in mEditTextsPassword.indices) {
                    passwords += mEditTextsPassword[i].text.toString() + append
                }

                copyToClipboard(passwords, mEditTextsPassword.size > 1)
            }
        }

        updateCharactersText()

        updatePasswordLayouts(true)
        generateAndShowPasswords()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSettings.destroyInstance()
    }

    override fun onResume() {
        super.onResume()

        if (mViewPasswordMasterAd != null) {
            mViewPasswordMasterAd!!.setAlpha(0f)
            mViewPasswordMasterAd!!.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null)
        }
    }

    override fun onPause() {
        super.onPause()
        AppSettings.instance.saveSettings()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCharactersText()
    }

    override fun onBackPressed() {
        if (AppSettings.instance.mAutoClipboardClearing && hasDataInClipboard()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(String.format(getString(R.string.dialog_quit_clipboard), getString(R.string.app_name), getString(R.string.app_name)))
            builder.setPositiveButton(getString(R.string.dialog_button_yes)) { dialog, id ->
                clearClipboard()
                finish()
            }
            builder.setNegativeButton(getString(R.string.dialog_button_no), null)
            builder.show()
        }
        else if (AppSettings.instance != null && !AppSettings.instance.mRateDialogShowed && AppSettings.instance.mApplicationLaunchCount >= 5) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(String.format(getString(R.string.dialog_quit_rate), getString(R.string.app_name)))
            builder.setPositiveButton(getString(R.string.dialog_button_rate_it)) { dialog, id ->
                AppSettings.instance.mRateDialogShowed = true
                Utils.openPlayStore(this@MainActivity, BuildConfig.APPLICATION_ID, 0)
                finish()
            }
            builder.setNegativeButton(getString(R.string.dialog_button_no_thanks)) { dialog, id ->
                AppSettings.instance.mRateDialogShowed = true
                finish()
            }
            builder.show()
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.auto_clipboard_clearing)?.setChecked(AppSettings.instance.mAutoClipboardClearing)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.auto_clipboard_clearing -> {
                AppSettings.instance.mAutoClipboardClearing = !AppSettings.instance.mAutoClipboardClearing
                item.setChecked(AppSettings.instance.mAutoClipboardClearing)
                return true
            }

            R.id.menu_reset_symbols -> {
                mEditTextSymbols!!.setText(AppSettings.DEFAULT_PASSWORD_SYMBOLS)
                return true
            }



            R.id.menu_tips -> {
                val builderTips = AlertDialog.Builder(this)
                builderTips.setTitle(R.string.main_activity_menu_tips)
                builderTips.setMessage(R.string.tips_text)
                builderTips.setPositiveButton(R.string.dialog_button_ok, null)
                builderTips.show()
                return true
            }

            R.id.menu_password_strength -> {
                val builderStrength = AlertDialog.Builder(this)
                builderStrength.setTitle(R.string.main_activity_menu_password_strength)
                builderStrength.setMessage(String.format(getString(R.string.dialog_password_strength_text), getString(R.string.app_name), getString(R.string.app_name)))
                builderStrength.setPositiveButton(R.string.dialog_button_ok, null)
                builderStrength.show()
                return true
            }

            R.id.menu_help -> {
                val builderHelp = AlertDialog.Builder(this)
                builderHelp.setTitle(R.string.main_activity_menu_help)
                builderHelp.setMessage(getString(R.string.dialog_help_text))
                builderHelp.setPositiveButton(R.string.dialog_button_ok, null)
                builderHelp.show()
                return true
            }

            R.id.menu_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }



    private fun restartActivity() {
        val packageManager: PackageManager = this.getPackageManager()
        val intent = packageManager.getLaunchIntentForPackage(this.getPackageName())
        val componentName = intent!!.component
        val mainIntent: Intent = Intent.makeRestartActivityTask(componentName)
        this.startActivity(mainIntent)
        finish()
    }

    private fun updatePasswordLayouts(focusFirst: Boolean) {
        mLinearLayoutPasswords!!.removeAllViews()

        mTextViewsStrength.clear()
        mProgressBarsStrength.clear()
        mEditTextsPassword.clear()

        for (i in 0 until AppSettings.instance.mPasswordsCount) {
            val layoutPassword = layoutInflater.inflate(R.layout.layout_password, null) as LinearLayout
            mLinearLayoutPasswords!!.addView(layoutPassword)

            // TextView
            val textViewStrength = layoutPassword.findViewById<View>(R.id.textViewStrength) as TextView

            // ProgressBar
            val progressBarStrength = layoutPassword.findViewById<View>(R.id.progressBarStrength) as ProgressBar
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mProgressBarStrength.setScaleY(1.5f);
            }*/

            // EditText
            val editTextPassword = layoutPassword.findViewById<View>(R.id.editTextPassword) as EditText

            if (editTextPassword != null) {
                editTextPassword.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(userEnteredValue: Editable) {

                    }

                    override fun onTextChanged(userEnteredValue: CharSequence, start: Int, before: Int, count: Int) {
                        val passwordStrengths = resources.getStringArray(R.array.other_password_strength)

                        val passwordEntropy = getPasswordEntropy(editTextPassword.text.toString())
                        val passwordStrength: String
                        val passwordStrengthIdx: Int
                        val color: Int

                        if (passwordEntropy >= 170) {
                            passwordStrengthIdx = 4
                            color = AppSettings.instance.getColor(R.attr.password_strength_ultra_strong)
                        }
                        else if (passwordEntropy >= 112) {
                            passwordStrengthIdx = 3
                            color = AppSettings.instance.getColor(R.attr.password_strength_very_strong)
                        }
                        else if (passwordEntropy >= 80) {
                            passwordStrengthIdx = 2
                            color = AppSettings.instance.getColor(R.attr.password_strength_strong)
                        }
                        else if (passwordEntropy >= 48) {
                            passwordStrengthIdx = 1
                            color = AppSettings.instance.getColor(R.attr.password_strength_medium)
                        }
                        else {
                            passwordStrengthIdx = 0
                            color = AppSettings.instance.getColor(R.attr.password_strength_weak)
                        }

                        passwordStrength = passwordStrengths[passwordStrengthIdx]

                        textViewStrength.setTextColor(color)
                        textViewStrength.text = getString(R.string.main_activity_textview_strength, passwordStrength, passwordEntropy)
                        progressBarStrength.progressDrawable.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
                        progressBarStrength.progress = Math.min(Math.round(passwordEntropy * (100.0f / 112.0f)), 100)
                    }

                    override fun beforeTextChanged(userEnteredValue: CharSequence, start: Int, before: Int, count: Int) {

                    }
                })
                if (i == 0 && focusFirst) {
                    editTextPassword.requestFocus()
                }
            }

            mTextViewsStrength.add(textViewStrength)
            mProgressBarsStrength.add(progressBarStrength)
            mEditTextsPassword.add(editTextPassword)
        }
    }

    private fun updateCharactersText() {
        val displayMetrics = Utils.getDisplayMetrics(this)
        val size = Utils.getDisplaySize(this)
        val widthDp = Utils.pxToDp(displayMetrics, size.x)

        if (widthDp < 400) {
            if (mCheckBoxUniqueChars != null) {
                mCheckBoxUniqueChars!!.setText(R.string.main_activity_chechbox_unique_chars)
            }

            if (mCheckBoxSimilarChars != null) {
                mCheckBoxSimilarChars!!.setText(R.string.main_activity_chechbox_similar_chars)
            }
        } else {
            if (mCheckBoxUniqueChars != null) {
                mCheckBoxUniqueChars!!.setText(R.string.main_activity_chechbox_unique_characters)
            }

            if (mCheckBoxSimilarChars != null) {
                mCheckBoxSimilarChars!!.setText(R.string.main_activity_chechbox_similar_characters)
            }
        }
    }

    private fun getPasswordEntropy(password: String): Int {
        var n = 0
        var uniqueCharacters = 0

        for (i in 0 until password.length) {
            if (i == password.indexOf(password[i])) {
                uniqueCharacters++
            }
        }

        var hasLowerCase = false
        var hasUpperCase = false
        var hasNumbers = false
        var otherCharacters = ""

        for (i in 0 until password.length) {
            val character = password[i].toString()

            if (!hasLowerCase && AppSettings.PASSWORD_LOWER_CASE.contains(character)) {
                n += AppSettings.PASSWORD_LOWER_CASE.length
                hasLowerCase = true
            }
            else if (!hasUpperCase && AppSettings.PASSWORD_UPPER_CASE.contains(character)) {
                n += AppSettings.PASSWORD_UPPER_CASE.length
                hasUpperCase = true
            }
            else if (!hasNumbers && AppSettings.PASSWORD_NUMBERS.contains(character)) {
                n += AppSettings.PASSWORD_NUMBERS.length
                hasNumbers = true
            }
            else if (!otherCharacters.contains(character)) {
                n++
                otherCharacters += character
            }
        }

        val passwordEntropy = (uniqueCharacters * (Math.log10(n.toDouble()) / Math.log10(2.0))).toInt()
        return if (passwordEntropy == 0 && !password.isEmpty()) 1 else passwordEntropy
    }

    private fun generateAndShowPasswords() {
        for (i in mEditTextsPassword.indices) {

            val password = generatePassword(AppSettings.instance.mPasswordLength)

            if (password.compareTo("") != 0) {
                mEditTextsPassword[i].setText(password)
                if (i == 0) {
                    mEditTextsPassword[i].setSelection(mEditTextsPassword[i].text.length)
                }
            }
        }
    }

    private fun generatePassword(length: Int): String {
        var length = length
        val random = mRandomSecure

        var uniqueCharactersCount = 0

        var characters = ""
        if (mCheckBoxLowerCase!!.isChecked) {
            characters += AppSettings.PASSWORD_LOWER_CASE
            uniqueCharactersCount += AppSettings.PASSWORD_LOWER_CASE.length
        }
        if (mCheckBoxUpperCase!!.isChecked) {
            characters += AppSettings.PASSWORD_UPPER_CASE
            uniqueCharactersCount += AppSettings.PASSWORD_UPPER_CASE.length
        }
        if (mCheckBoxNumbers!!.isChecked) {
            characters += AppSettings.PASSWORD_NUMBERS
            uniqueCharactersCount += AppSettings.PASSWORD_NUMBERS.length
        }
        if (mCheckBoxSymbols!!.isChecked) {
            val symbols = mEditTextSymbols!!.text.toString().trim { it <= ' ' }
            var uniqueCharacters = characters

            for (i in 0 until symbols.length) {
                val symbol = symbols.get(i).toString()

                if (!uniqueCharacters.contains(symbol)) {
                    uniqueCharacters += symbol
                    uniqueCharactersCount++
                }
            }

            characters += symbols
        }

        if (!mCheckBoxSimilarChars!!.isChecked) {
            for (i in AppSettings.SIMILAR_CHARS.indices) {
                val currentSimilarChars = AppSettings.SIMILAR_CHARS[i]

                for (j in 0 until currentSimilarChars.length) {
                    val countOriginal = characters.length
                    characters = characters.replace(currentSimilarChars[j].toString().toRegex(), "")

                    if (countOriginal > characters.length) {
                        uniqueCharactersCount--
                    }
                }
            }
        }

        var password = ""

        if (characters != "") {
            length = Math.max(length, AppSettings.MIN_PASSWORD_LENGTH)

            for (i in 0 until length) {
                var rnd = random.nextInt(characters.length)
                var c = characters[rnd].toString()

                if (mCheckBoxUniqueChars!!.isChecked && password.length < uniqueCharactersCount) {
                    while (password.contains(c)) {
                        rnd = random.nextInt(characters.length)
                        c = characters[rnd].toString()
                    }
                }

                password += c
            }
        }

        return password
    }

    fun hasDataInClipboard(): Boolean {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard.primaryClip?.itemCount != null && clipboard.primaryClip?.itemCount?.compareTo(0) != 0 && clipboard.primaryClip?.getItemAt(0)?.text != ""
    }

    private fun copyToClipboard(data: String, plural: Boolean) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(AppSettings.CLIPBOARD_LABEL, data)
        clipboard.setPrimaryClip(clip)

        var toastText = if (plural) getString(R.string.main_activity_info_copied_to_clipboard_plural) else getString(R.string.main_activity_info_copied_to_clipboard_singular)
        toastText += if (AppSettings.instance.mAutoClipboardClearing) " " + getString(R.string.main_activity_info_clear_clipboard_timeout) else ""
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()

        if (AppSettings.instance.mAutoClipboardClearing) {
            Handler().postDelayed({
                clearClipboard()
            }, AppSettings.CLIPBOARD_CLEAR_DELAY)
        }
    }

    private fun clearClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            clipboard.clearPrimaryClip()
        }
        else {
            val emptyClip = ClipData.newPlainText(AppSettings.CLIPBOARD_LABEL, "")
            clipboard.setPrimaryClip(emptyClip)
        }
    }

}



