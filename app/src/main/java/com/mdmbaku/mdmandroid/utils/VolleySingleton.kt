package com.mdmbaku.mdmandroid.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.util.LruCache
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.HttpStack
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import java.security.GeneralSecurityException

internal class VolleySingleton private constructor(private val mCtx: Context) {

    private var mRequestQueue: RequestQueue? = null
    val imageLoader: ImageLoader

    private// getApplicationContext() is key, it keeps you from leaking the
    // Activity or BroadcastReceiver if someone passes one in.
    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

                    var stack: HttpStack

                    try {
                        stack = HurlStack(null, TLSSocketFactory())
                    } catch (e: GeneralSecurityException) {
                        stack = HurlStack()
                    }

                    mRequestQueue = Volley.newRequestQueue(mCtx, stack)
                } else {
                    mRequestQueue = Volley.newRequestQueue(mCtx)
                }
            }

            return mRequestQueue
        }

    init {
        mRequestQueue = requestQueue

        imageLoader = ImageLoader(this.mRequestQueue, object : ImageLoader.ImageCache {
            private val cacheSize = 4 * 1024 * 1024 // 4MiB
            private val mCache = LruCache<String, Bitmap>(cacheSize)
            override fun putBitmap(url: String, bitmap: Bitmap) {
                mCache.put(url, bitmap)
            }

            override fun getBitmap(url: String): Bitmap {
                return mCache.get(url)
            }
        })
    }

    fun <T> addToRequestQueue(req: Request<T>) {

        val TIMEOUT_MS = 25 * 1000

        req.retryPolicy = DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        requestQueue?.add(req)
    }

    fun cancelAll() {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll { true }
        }
    }

    companion object {

        @Volatile
        private var vInstance: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton? {

            if (vInstance == null) {
                synchronized(VolleySingleton::class.java) {
                    if (vInstance == null) {
                        vInstance = VolleySingleton(context.applicationContext)
                    }
                }
            }

            return vInstance
        }
    }
}