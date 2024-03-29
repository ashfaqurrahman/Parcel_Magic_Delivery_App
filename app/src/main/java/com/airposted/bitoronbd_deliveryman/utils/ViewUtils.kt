package com.airposted.bitoronbd_deliveryman.utils

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.view.WebViewActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode


fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show(){
    visibility = View.VISIBLE
}

fun ProgressBar.hide(){
    visibility = View.GONE
}

fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

var pDialog: ProgressDialog? = null

fun setProgressDialog(context: Context) {
    try {
        pDialog = ProgressDialog(context)
        pDialog!!.setCancelable(false)
        pDialog!!.setCanceledOnTouchOutside(false)
        pDialog!!.show()
        pDialog!!.setContentView(R.layout.custom_progress_bar)
        pDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pDialog!!.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    } catch (ignored: Exception) {
    }
}

fun dismissDialog() {
    try {
        if (pDialog != null && pDialog!!.isShowing) {
            pDialog!!.dismiss()
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        pDialog = null
    }
}

fun zeroRemove(phone: String): String {
    var phoneNo = phone
    if (phoneNo.startsWith("0")) {
        phoneNo = phoneNo.substring(1)
    }
    return phoneNo
}

fun round(value: Double, places: Int): Double {
    require(places >= 0)
    var bd: BigDecimal = BigDecimal.valueOf(value)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun otpWatcher(context: Context, input: OtpTextView, button: TextView): String? {
    button.isEnabled = false
    var otp1: String? = null
    input.otpListener = object : OTPListener {
        override fun onInteractionListener() {
            button.isEnabled = false
            button.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.before_button_bg
            )
            input.resetState()
        }

        override fun onOTPComplete(otp: String) {
            otp1 = otp
            button.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.after_button_bg
            )
            button.isEnabled = true
        }
    }
    return otp1
}

fun textWatcher(context: Context, size: Int, input: EditText, button: TextView) {
    button.isEnabled = false
    input.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int,
            before: Int, count: Int
        ) {
            if (s.length > size) {
                button.background = ContextCompat.getDrawable(context, R.drawable.after_button_bg)
                button.isEnabled = true
            } else {
                button.background = ContextCompat.getDrawable(context, R.drawable.before_button_bg)
                button.isEnabled = false
            }
        }
    })
}

fun multipleTextWatcher(context: Context, phone: EditText, password: EditText, button: TextView, size0: Int, size1: Int) {
    var phoneLength = 0
    var passwordLength = 0
    phone.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int,
            before: Int, count: Int
        ) {
            phoneLength = s.length
            updateSingInButton(context, phoneLength, passwordLength, size0, size1, button)
        }
    })
    password.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int,
            before: Int, count: Int
        ) {
            passwordLength = s.length
            updateSingInButton(context, phoneLength, passwordLength, size0, size1, button)
        }
    })
}

private fun updateSingInButton(context: Context, phoneLength: Int, passwordLength: Int, size0: Int, size1: Int, button: TextView) {
    if (phoneLength == size0 && passwordLength >= size1) {
        button.background = ContextCompat.getDrawable(context, R.drawable.after_button_bg)
        button.isEnabled = true
    } else {
        button.background = ContextCompat.getDrawable(context, R.drawable.before_button_bg)
        button.isEnabled = false
    }
}

fun locationTextWatcher(size: Int, input: EditText): String {
    var text = ""
    input.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(
            s: CharSequence?, start: Int,
            count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int,
            before: Int, count: Int
        ) {
            text = if (s.length > size) {
                s.toString()
            } else {
                ""
            }
        }
    })

    return text
}

fun customTextView(view: TextView, context: Context) {
    val spanTxt = SpannableStringBuilder(
        context.getString(R.string.by_useing)
    )
    spanTxt.append(" ").append(context.getString(R.string.terms_of_service))
    spanTxt.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            AppHelper.webviewTitle = context.getString(R.string.terms_of_service)
            val bundle = Bundle()
            bundle.putString(AppHelper.DETAILS_KEY, AppHelper.TERMS)
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }, spanTxt.length - context.getString(R.string.terms_of_service).length, spanTxt.length, 0)
    spanTxt.setSpan(
        ForegroundColorSpan(
            ContextCompat.getColor(
                context, R.color.blue
            )
        ),
        context.getString(R.string.by_useing).length,
        spanTxt.length,
        0
    )
    spanTxt.append(" ").append(context.getString(R.string.and))
    spanTxt.append(" ").append(context.getString(R.string.privacy_policy))
    spanTxt.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            AppHelper.webviewTitle = context.getString(R.string.privacy_policy)
            val bundle = Bundle()
            bundle.putString(AppHelper.DETAILS_KEY, AppHelper.PEIVACY_POLICY)
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }, spanTxt.length - context.getString(R.string.privacy_policy).length, spanTxt.length, 0)
    view.movementMethod = LinkMovementMethod.getInstance()
    spanTxt.setSpan(
        ForegroundColorSpan(
            ContextCompat.getColor(
                context, R.color.blue
            )
        ),
        context.getString(R.string.by_useing).length + context.getString(R.string.and).length + context.getString(
            R.string.terms_of_service
        ).length + 2,
        spanTxt.length,
        0
    )
    view.setText(spanTxt, TextView.BufferType.SPANNABLE)
}

fun reduceImageSize(file: File): File? {
    return try {

        // BitmapFactory options to downsize the image
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        o.inSampleSize = 6
        // factor of downsizing the image
        var inputStream = FileInputStream(file)
        //Bitmap selectedBitmap = null;
        BitmapFactory.decodeStream(inputStream, null, o)
        inputStream.close()

        // The new size we want to scale to
        val REQUIRED_SIZE = 55

        // Find the correct scale value. It should be the power of 2.
        var scale = 1
        while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
            o.outHeight / scale / 2 >= REQUIRED_SIZE
        ) {
            scale *= 2
        }
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        inputStream = FileInputStream(file)
        val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
        inputStream.close()

        // here i override the original image file
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        file
    } catch (e: java.lang.Exception) {
        null
    }
}
