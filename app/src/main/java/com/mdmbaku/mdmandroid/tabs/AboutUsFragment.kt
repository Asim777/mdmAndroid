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
    private var listUrl = mutableListOf<String>()
    private var listText = mutableListOf<String>()
    private var realm: Realm = Realm.getDefaultInstance()
    private var mAboutUsPage: WpPage? = null
    private var mClientLogosPage: WpPage? = null
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
            Network.getInstance().requestClientLogos(context!!, this, Network.Companion.RequestType.REQUEST_CLIENT_LOGOS)
        }

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/HovsanCity.png")
        listText.add("Rational organization of space needed to any human activities Design With Us")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/OIK.png")
        listText.add("Functionality Flexibility Economic efficiency")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/KacmasBusinessDeveloppingCenter.png")
        listText.add("Protection of ecological balance eco culture clear recycling ")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/LenkoranMotel.png")
        listText.add("Design includes Sustainability Longevity")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/Basketbol4.png")
        listText.add("Aesthetic Ergonomically Comfortable environment")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/BakuComplex6.png")
        listText.add("A modern approach to traditional design Aesthetic Comfortable measurement")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/Gonche.png")
        listText.add("")

        listUrl.add("http://mdmbaku.com/wp-content/uploads/2018/03/KorpuAlov6.png")
        listText.add("")

        // initialize SliderLayout
        for (i in listUrl.indices) {
            val sliderView = TextSliderView(activity)
            sliderView
                    .image(listUrl[i])
                    .description(listText[i] + "\n")
                    .setProgressBarVisible(true)
                    .setRequestOption(RequestOptions().centerCrop())
                    .setBackgroundColor(Color.WHITE)

            mMainSlider.addSlider(sliderView)
        }

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {

        if (requestType == Network.Companion.RequestType.REQUEST_ABOUT_US) {
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
        } else if (requestType == Network.Companion.RequestType.REQUEST_CLIENT_LOGOS) {
            val clientLogosPage: WpPage = gson.fromJson(jsonObject.toString(), WpPage::class.java)

            try {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(clientLogosPage)
                realm.commitTransaction()

                updateClientLogosPage()
                renderAboutUsContent()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Realm error", e.message)
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

        if (mClientLogosPage != null) {
            // horizontal and cycled carousel layout
            val logos = getClientLogos()

            if (logos != null) {
                // initialize SliderLayout
                for (i in logos.indices) {
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

    private fun getClientLogos(): List<String>? {
        val clientLogosContent = mClientLogosPage?.content?.renderedContent
        val logosList = clientLogosContent
                ?.substring(3, clientLogosContent.length - 5)
                ?.split("<br />")

        return logosList?.map {it ->
            if (it.startsWith("\n")) {
                it.substring(1)
            } else {
                it
            }
        }
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
