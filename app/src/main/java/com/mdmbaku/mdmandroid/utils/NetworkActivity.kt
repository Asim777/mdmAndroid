package com.mdmbaku.mdmandroid.utils

import android.app.ProgressDialog
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.mdmbaku.mdmandroid.R
import org.json.JSONObject

abstract class NetworkActivity : AppCompatActivity() {

    private var confirmationDialog: AlertDialog? = null
    private var dialog: ProgressDialog? = null

    fun onLoading(isLoading: Boolean) {

        if (isLoading) {
            dialog = ProgressDialog(this)
            dialog!!.setMessage(getString(R.string.loading))
            dialog!!.setCancelable(false)
            dialog!!.show()
        } else {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        }
    }

    fun onError(errorMessage: String, orderId: String, resultCode: Int) {

        if (isFinishing) {
            return
        }
        /*when (resultCode) {

        }*/
    } //onError



    override fun onDestroy() {
        super.onDestroy()

        if (dialog != null) {
            dialog!!.dismiss()
        }
        if (confirmationDialog != null) {
            confirmationDialog!!.dismiss()
        }
        Network.getInstance().cancelAll(this)
    }

    abstract fun networkActivity(newActivity: Class<*>, jsonObj: JSONObject)
}