package com.mdmbaku.mdmandroid.tabs

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.ApplicationClass
import com.mdmbaku.mdmandroid.HomeActivity
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.WpPage
import com.mdmbaku.mdmandroid.utils.IDataForFragment
import com.mdmbaku.mdmandroid.utils.Network
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_contact_us.*
import org.json.JSONObject
import java.io.StringReader

private var gson: Gson = Gson()
private lateinit var phoneToCall: String
private var mContactPage: WpPage? = null

class ContactUsFragment : Fragment(), IDataForFragment {
    private var realm: Realm = Realm.getDefaultInstance()
    private lateinit var mContactUsEmail: TextView
    private lateinit var mContactUsPhone: TextView
    private lateinit var mContactUsFax: TextView
    private lateinit var mContactUsAddress: TextView
    private lateinit var mContactUsContent: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_contact_us, container, false)
        mContactUsEmail = rootView.findViewById(R.id.contact_us_email_value)
        mContactUsPhone = rootView.findViewById(R.id.contact_us_phone_value)
        mContactUsFax = rootView.findViewById(R.id.contact_us_fax_value)
        mContactUsAddress = rootView.findViewById(R.id.contact_us_address_value)
        mContactUsContent = rootView.findViewById(R.id.contact_us_content)

        if (mContactPage != null) {
            renderContactPage()
        }

        if ((activity as HomeActivity).isNetworkAvailable()) {
            Network.getInstance().requestContactUsPage(context!!, this, Network.Companion.RequestType.REQUEST_CONTACT_US)
        }

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {
        if (requestType == Network.Companion.RequestType.REQUEST_CONTACT_US) {
            val contactUsPageStringReader = StringReader(jsonObject.toString())
            val contactUsPage: WpPage = gson.fromJson(contactUsPageStringReader, WpPage::class.java)

            try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(contactUsPage)
                realm.commitTransaction()
                updateContactPage()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Realm error", e.message)
            }

            renderContactPage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateContactPage()
    }

    private fun updateContactPage() {
        mContactPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.CONTACT_US.pageId).findFirst()
    }

    private fun launchMap(locationLable: String) {
        val urlAddress = "https://www.google.com/maps/search/?api=1&query=$locationLable)"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress))
        startActivity(intent)
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "info@mdm-az.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MDM - From Android App")
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun showCallDialog() {
        if (mContactUsPhone.text.isNotEmpty() && checkCallPermission()) {
            val dialog = AlertDialog.Builder(activity as Activity)
            dialog.setTitle(resources.getString(R.string.confirm_call_title))
            dialog.setMessage(resources.getString(R.string.confirm_call_text))
            dialog.setPositiveButton(resources.getString(R.string.call)) { _, _ ->
                makeCall()
            }

            dialog.setNegativeButton(getString(R.string.cancel)) { d, _ ->
                d.dismiss()
            }
            dialog.show()
        }
    }

    private fun checkCallPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(activity as Activity)
                        .setTitle(R.string.title_call_permission)
                        .setMessage(R.string.text_call_permission)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            //Prompt the user once explanation has been shown
                            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
                                    PERMISSION_REQUEST_CALL)
                        }
                        .create()
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSION_REQUEST_CALL)
            }
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_REQUEST_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity as Activity,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        makeCall()
                    }
                } else {
                    Toast.makeText(ApplicationClass.getAppContext(), R.string.call_permission_denied, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun makeCall() {
        if (phoneToCall.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneToCall")
            startActivity(intent)
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CALL = 10
    }


    private fun renderContactPage() {
        val contactUsContent = mContactPage?.content?.renderedContent

        if (!contactUsContent.isNullOrBlank()) {
            val endOfHeader = contactUsContent!!.indexOf("<p class=\"Email\">")
            val contactUsContentHeader = contactUsContent.substring(0, endOfHeader)

            val endOfEmail = contactUsContent.indexOf("</p>", endOfHeader)
            val contactUsEmail = contactUsContent.substring(endOfHeader + 17, endOfEmail)
            val spannableEmail = SpannableString(contactUsEmail)
            spannableEmail.setSpan(UnderlineSpan(), 0,
                    spannableEmail.length, 0)

            val endOfPhone = contactUsContent.indexOf("</p>", endOfEmail + 3)
            val contactUsPhone = contactUsContent.substring(contactUsContent.indexOf(
                    "<p class=\"Phone\">") + 17, endOfPhone)
            val spannablePhone = SpannableString(contactUsPhone)
            spannablePhone.setSpan(UnderlineSpan(), 0,
                    spannablePhone.length, 0)

            val endOfFax = contactUsContent.indexOf("</p>", endOfPhone + 3)
            val contactUsFax = contactUsContent.substring(contactUsContent.indexOf(
                    "<p class=\"Fax\">") + 15, endOfFax)
            val spannableFax = SpannableString(contactUsFax)
            spannableFax.setSpan(UnderlineSpan(), 0,
                    contactUsFax.length, 0)

            val endOfAddress = contactUsContent.indexOf("</p>", endOfFax + 3)
            val contactUsAddress = contactUsContent.substring(contactUsContent.indexOf(
                    "<p class=\"Address\">") + 19, endOfAddress)
            val spannableAddress = SpannableString(contactUsAddress)
            spannableAddress.setSpan(UnderlineSpan(), 0,
                    spannableEmail.length, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mContactUsContent.text = Html.fromHtml(contactUsContentHeader, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mContactUsContent.text = Html.fromHtml(contactUsContentHeader)
            }

            mContactUsEmail.text = spannableEmail
            mContactUsPhone.text = spannablePhone
            mContactUsFax.text = spannableFax
            mContactUsAddress.text = spannableAddress

            mContactUsPhone.setOnClickListener {
                phoneToCall = mContactUsPhone.text.toString()
                showCallDialog()
            }

            mContactUsFax.setOnClickListener {
                phoneToCall = mContactUsPhone.text.toString()
                showCallDialog()
            }

            mContactUsEmail.setOnClickListener {
                sendEmail()
            }

            mContactUsAddress.setOnClickListener {
                launchMap(contactUsAddress)
            }
        }
    }
}