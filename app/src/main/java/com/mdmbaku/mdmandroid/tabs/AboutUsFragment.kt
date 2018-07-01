package com.mdmbaku.mdmandroid.tabs

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.DefaultSliderView
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.ApplicationClass
import com.mdmbaku.mdmandroid.HomeActivity
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.WpPage
import com.mdmbaku.mdmandroid.utils.IDataForFragment
import com.mdmbaku.mdmandroid.utils.Network
import io.realm.Realm
import org.json.JSONObject


private lateinit var gson: Gson

class AboutUsFragment : Fragment(), ViewPagerEx.OnPageChangeListener, IDataForFragment {
    private var realm: Realm = Realm.getDefaultInstance()
    private var mAboutUsPage: WpPage? = null
    private var mClientLogosPage: WpPage? = null
    private var mTopSliderPage: WpPage? = null
    private lateinit var mMainSlider: SliderLayout
    private lateinit var mClientsSlider: SliderLayout
    private lateinit var mAboutUsTitleTextView: TextView
    private lateinit var mClientsTitleTextView: TextView
    private lateinit var mAboutUsContentTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_about_us, container, false)
        mMainSlider = rootView.findViewById(R.id.main_slider) as SliderLayout
        mClientsSlider = rootView.findViewById(R.id.clients_slider) as SliderLayout
        mAboutUsTitleTextView = rootView.findViewById(R.id.about_us_title)
        mAboutUsContentTextView = rootView.findViewById(R.id.about_us_content)
        mClientsTitleTextView = rootView.findViewById(R.id.clients_title)

        mAboutUsTitleTextView.typeface = ApplicationClass.getAppInstance()?.getBoldTypeface()
        mClientsTitleTextView.typeface = ApplicationClass.getAppInstance()?.getBoldTypeface()
        mAboutUsContentTextView.typeface = ApplicationClass.getAppInstance()?.getRegularTypeface()

        if (mAboutUsPage != null && !mAboutUsPage?.content.toString().isBlank() &&
                !mAboutUsPage?.content?.renderedContent!!.isBlank()) {
            renderAboutUsContent()
        }

        if ((activity as HomeActivity).isNetworkAvailable()) {
            Network.getInstance().requestAboutUsPage(context!!, this, Network.Companion.RequestType.REQUEST_ABOUT_US)
            Network.getInstance().requestSlider(context!!, this, Network.Companion.RequestType.REQUEST_SLIDER)
            Network.getInstance().requestClientLogos(context!!, this, Network.Companion.RequestType.REQUEST_CLIENT_LOGOS)
        }

        updateTopSliderPage()
        updateClientLogosPage()
        updateTopSlider()
        updateClientsSlider()

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {

        when (requestType) {
            Network.Companion.RequestType.REQUEST_ABOUT_US -> {
                val aboutUsPage: WpPage = gson.fromJson(jsonObject.toString(), WpPage::class.java)

                try {
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(aboutUsPage)
                    realm.commitTransaction()

                    updateAboutUsPage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Realm error", e.message)
                }

                renderAboutUsContent()
            }

            Network.Companion.RequestType.REQUEST_SLIDER -> {
                val sliderPage: WpPage = gson.fromJson(jsonObject.toString(), WpPage::class.java)

                try {
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(sliderPage)
                    realm.commitTransaction()

                    updateTopSliderPage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Realm error", e.message)
                }

                updateTopSlider()
            }

            Network.Companion.RequestType.REQUEST_CLIENT_LOGOS -> {
                val clientLogosPage: WpPage = gson.fromJson(jsonObject.toString(), WpPage::class.java)

                try {
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(clientLogosPage)
                    realm.commitTransaction()

                    updateClientLogosPage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Realm error", e.message)
                }

                updateClientsSlider()
            }
            else -> {
            }
        }
    }

    private fun updateAboutUsPage() {
        mAboutUsPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.ABOUT_US.pageId).findFirst()
    }

    private fun updateClientLogosPage() {
        mClientLogosPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.CLIENT_LOGOS.pageId).findFirst()
    }

    private fun updateTopSliderPage() {
        mTopSliderPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.SLIDER.pageId).findFirst()
    }

    private fun updateTopSlider() {
        if (mTopSliderPage != null) {
            val topSliderList = getTopSliderItems()

            if (topSliderList != null) {

                // initialize SliderLayout
                mMainSlider.removeAllSliders()
                for ((image, description) in topSliderList) {
                    val sliderView = TextSliderView(activity)
                    sliderView
                            .image(image)
                            .description(description + "\n")
                            .setProgressBarVisible(true)
                            .setRequestOption(RequestOptions().centerCrop())
                            .setBackgroundColor(Color.WHITE)

                    mMainSlider.addSlider(sliderView)
                }
            }
        }
    }

    private fun updateClientsSlider() {
        if (mClientLogosPage != null) {
            val logos = getClientLogos()

            if (logos != null) {
                // initialize SliderLayout
                mClientsSlider.removeAllSliders()

                for (i in logos.indices) {
                    // horizontal and cycled carousel layout
                    val sliderView = DefaultSliderView(activity)
                    sliderView
                            .image(logos[i])
                            .setProgressBarVisible(true)
                            .setRequestOption(RequestOptions().centerInside())
                            .setBackgroundColor(Color.WHITE)

                    mClientsSlider.addSlider(sliderView)
                }
            }
        }
    }

    private fun renderAboutUsContent() {
        val aboutUsTitle = mAboutUsPage?.title?.renderedTitle
        if (aboutUsTitle != null && mAboutUsPage?.content != null) {
            val renderedContent = mAboutUsPage?.content?.renderedContent
            val aboutUsContent = renderedContent
                    ?.substring(0, renderedContent.indexOf("<h3></h3>\n<h3><strong>Clients"))
                    ?.removeListIcons()

            mAboutUsTitleTextView.text = aboutUsTitle

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mAboutUsContentTextView.text = Html.fromHtml(aboutUsContent, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mAboutUsContentTextView.text = Html.fromHtml(aboutUsContent)
            }
        }
    }

    private fun getClientLogos(): List<String>? {
        val clientLogosContent = mClientLogosPage?.content?.renderedContent
        val logosList = clientLogosContent
                ?.substring(3, clientLogosContent.length - 5)
                ?.split("<br />")

        return logosList?.map { it ->
            if (it.startsWith("\n")) {
                it.substring(1)
            } else {
                it
            }
        }
    }

    private fun getTopSliderItems(): Map<String, String>? {
        val resultMap = mutableMapOf<String, String>()
        val sliderContent = mTopSliderPage?.content?.renderedContent
        var sliderItemsList = sliderContent
                ?.substring(3, sliderContent.length - 5)
                ?.split("<br />")

        sliderItemsList = sliderItemsList?.map { it ->
            if (it.startsWith("\n")) {
                it.substring(1)
            } else {
                it
            }
        }

        if (sliderItemsList != null) {
            for (sliderItem in sliderItemsList) {
                val sliderItemSplitted = sliderItem.split(",")
                resultMap[sliderItemSplitted[0]] = sliderItemSplitted[1]
            }
        }
        return resultMap
    }

    private fun String.removeListIcons(): String {
        return this.replace("<ul>", "")
                .replace("</ul>", "")
                .replace("<li>", "<p>")
                .replace("</li>", "</p>")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        gson = Gson()
    }

    override fun onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mMainSlider.stopAutoCycle()
        super.onStop()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateAboutUsPage()
    }
}
