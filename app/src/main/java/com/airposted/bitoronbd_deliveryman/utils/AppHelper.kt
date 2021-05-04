package com.airposted.bitoronbd_deliveryman.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URI
import java.net.URISyntaxException
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern

object AppHelper {
    const val DETAILS_ID_KEY = "DETAILS_ID_KEY"
    const val DETAILS_KEY = "DETAILS_KEY"
    const val DETAILS_KEY1 = "DETAILS_KEY1"
    const val SHIPPING_KEY = "SHIPPING_KEY"
    const val TOTAL_KEY = "TOTAL_KEY"
    const val SUB_TOTAL_KEY = "SUB_TOTAL_KEY"
    const val DISCOUNT_KEY = "DISCOUNT_KEY"
    const val SETTINGS_DATA = "SETTINGS_DATA"
    const val OPEN_SCREEN_LOAD = "OPEN_SCREEN_LOAD"
    const val BUY_NOW = "Buy_NOW"
    const val BUY_NOW_PRODUCT = "BUY_NOW_PRODUCT"
    var isSearch = false
    var tempClassName = ""
    const val airposted_lat = 23.786474
    const val airposted_lng = 90.403455
    const val latitude = "latitude"
    const val longitude = "longitude"
    var webviewTitle = ""
    const val TERMS = "https://airposted.com/terms"
    const val AIRPOSTED_LIVE_WEB = "https://airposted.com"
    const val PEIVACY_POLICY = "https://airposted.com/privacy-policy"
    const val TEMP_API = "https://airposted-temp.firebaseio.com/status.json"
    const val paging_limit = 12
    const val raf_paging_limit = 150
    const val MAX_ITEM_COUNT = 5
    var isPhoneVerify = false
    fun isValidEmailAddress(email: String?): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    //public static int CURRENT_AIRPOSTED_TYPE = AirPostedType.SHOP.getValue();
    const val SEARCH_KEY = "SEARCH_KEY"

    //public static SettingsModel settingsModel;
    var countryCode = "BD"
    var currency = "$"
    const val LAST_CURRENCY = "LAST_CURRENCY"
    var currentCurrencyConversionRate = 1.0
    fun sentToBrowser(context: Context, url: String?) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }

    //public static MutableLiveData<CartCountModel> cartCount = new MutableLiveData<>();
    var notficationCount = 0
    fun convertDecimal(amount: Double): String {
        val dtime = DecimalFormat("0.00")
        return dtime.format(amount)
    }

    var userId: Long = 0
    var isEaringFragment = false
    fun urlShorter(fullUrl: String): String? {
        var url: String? = ""
        val finalUrl = fullUrl.trim { it <= ' ' }
        if (finalUrl != null) {
            var uri: URI? = null
            try {
                uri = URI(finalUrl)
                url = Objects.requireNonNull(uri).host
                if (url != null) {
                    url = if (url.startsWith("www.")) url.substring(4) else url
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        return url
    }

    fun zeroRemove(phoneNo: String): String {
        var phoneNo = phoneNo
        if (phoneNo.startsWith("0")) {
            phoneNo = phoneNo.substring(1)
        }
        if (phoneNo.startsWith("880")) {
            phoneNo = phoneNo.substring(3)
        }
        return phoneNo
    }

    var backButtonControl = false
    const val isMenuSelected = "isMenuSelected"

    //        return BuildConfig.VERSION_NAME;
    val appVersionName: String
        get() =//        return BuildConfig.VERSION_NAME;
            "1.5"

    const val help = "<b>How it works/ Help</b>" +

            "<br><br><b><u>Step 1</u></b><br>" +
            "<b>Got a bike/cycle?</b><br>" +
            "Register as a courier & deliver parcel around the city. <br>" +
            "<br>" +

            "<b><u>Step 2</u></b><br>" +
            "<b>Deliver & Earn</b><br>" +
            "Collect & deliver a parcel. Get paid while you’re at it.<br>" +
            "<br>" +

            "<br><br><b>FAQ</b>" +
            "<br><br><b>1. How can I become a freelancer? What are the requirements? </b>" +
            "<br><br>To become a freelancer, you need to visit our head office and register as a freelancer. And provide the documents listed below- " +
            "<br><br>For Bike-" +
            "<br>• NID" +
            "<br>• Driving license" +
            "<br>• Bike registration documents" +
            "<br>• Bike Tax Token" +
            "<br>• Bike insurance paper" +
            "<br>• Owners NID (If you’re not the owner of the bike)" +
            "<br><br>For Cycle-" +
            "<br>• NID" +

            "<br><br><b>2. How can I receive my payment? </b>" +
            "<br><br>Freelancer will receive their payment the next day into their account. You can withdraw the balance using your (MFS) mobile financial services." +

            "<br><br><b>3. How can I report an issue? </b>" +
            "<br><br>If you want to raise an issue to a specific delivery request, go to Account > Delivery History > Report Issue. And then select from the list of issues. If your issue is not listed there, you can call our Helpline number-" +
            "<br><br>Dhaka: 09777666555" +
            "<br><br>Or email us your issue at support@airpostedparcel.com" +

            "<br><br><b>4. I didn’t get the right fare. How can I adjust?</b>" +
            "<br><br>If you didn’t receive the right fare, you have to raise an issue. After that, we’ll check how much distance you’ve traveled and if the fare needs any adjustment. We’ll adjust the amount into your freelancer account. " +
            "<br><br>To raise an issue-" +
            "<br><br>Go to Account > Delivery History > Report Issue > Fare adjustment" +

            "<br><br><b>5. I’m not getting any requests. What should I do?</b>" +
            "<br><br>• Go to your phone settings > Sound and notifications> Notification settings > App Manager/Notifications manager > Search for ‘Airposted Parcel’ App > Select ‘Allow Notification, Priority & Sound notification’" +
            "<br><br>• Always turn on your ‘Location Permission’ and set it to high accuracy mode." +
            "<br><br>• Clear your cache and restart the App." +
            "<br><br>• If the problem still persists, uninstall your App and reinstall it from Google Play store."

    const val termsConditions = "1. To join as a freelancer, you must be at least 18 years old." +
            "<br><br>2. Airposted Parcel will not be liable if you accept and carry prohibited items." +
            "<br><br>3. Your documents (driving license, vehicle license, etc.) should be updated regularly." +
            "<br><br>4. You shall be solely responsible for any and all claims, judgments, and liabilities resulting from any accident, loss or damage." +
            "<br><br>5. You shall obey all local laws related to the operation of passenger transport and/or delivery services and will be solely responsible for any violations of such laws."+
            "<br><br>6. You shall not contact customers for personal interest or purposes other than the service."
}