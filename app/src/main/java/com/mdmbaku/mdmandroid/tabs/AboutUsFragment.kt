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
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx
import com.glide.slider.library.svg.GlideApp
import com.google.gson.Gson
import com.mdmbaku.mdmandroid.HomeActivity
import com.mdmbaku.mdmandroid.R
import com.mdmbaku.mdmandroid.data.WpPage
import com.mdmbaku.mdmandroid.utils.IDataForFragment
import com.mdmbaku.mdmandroid.utils.Network
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_about_us.*
import org.json.JSONObject

private lateinit var gson: Gson

class AboutUsFragment : Fragment(), ViewPagerEx.OnPageChangeListener, IDataForFragment {
    private var listUrl = mutableListOf<String>()
    private var listText = mutableListOf<String>()
    private var realm: Realm = Realm.getDefaultInstance()
    private var mAboutUsPage: WpPage? = null
    private lateinit var mSlider: SliderLayout
    private lateinit var mPartnersLogosImageView: ImageView
    private lateinit var mAboutUsTitleTextView: TextView
    private lateinit var mAboutUsContentTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_about_us, container, false)
        mSlider = rootView.findViewById(R.id.slider)
        mPartnersLogosImageView = rootView.findViewById(R.id.partners_logos)
        mAboutUsTitleTextView = rootView.findViewById(R.id.about_us_title)
        mAboutUsContentTextView = rootView.findViewById(R.id.about_us_content)

        if ((activity as HomeActivity).isNetworkAvailable()) {
            Network.getInstance().requestAboutUsPage(context!!, this, Network.Companion.RequestType.REQUEST_ABOUT_US)
        } else {
            if (mAboutUsPage != null) {
                renderAboutUsContent()
            }
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

        // if you want show image only / without description text use DefaultSliderView instead
        val requestOptions = RequestOptions()
        requestOptions.centerCrop()

        // initialize SliderLayout
        for (i in listUrl.indices) {
            val sliderView = TextSliderView(activity)
            sliderView
                    .image(listUrl[i])
                    .description(listText[i]+"\n")
                    .setProgressBarVisible(true)
                    .setRequestOption(requestOptions)
                    .setBackgroundColor(Color.WHITE)

            mSlider.addSlider(sliderView)
        }

        return rootView
    }

    override fun dataForFragment(jsonObject: JSONObject, requestType: Network.Companion.RequestType) {

        if (requestType == Network.Companion.RequestType.REQUEST_ABOUT_US) {
            val aboutUsPage: WpPage = gson.fromJson(jsonObject.toString(), WpPage::class.java)

            try {
                realm.beginTransaction()
                /*val recordsToRemove = realm.where(WpPage :: class.java).equalTo("id", aboutUsPage.id).findAll()
                recordsToRemove.deleteAllFromRealm()*/
                realm.copyToRealmOrUpdate(aboutUsPage)
                realm.commitTransaction()

                updateAboutUsPage()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Realm error", e.message)
            }

            renderAboutUsContent()
        }

    }

    private fun updateAboutUsPage() {
        mAboutUsPage = realm.where(WpPage::class.java).equalTo("id",
                Network.Companion.WpPageId.ABOUT_US.pageId).findFirst()
    }

    private fun renderAboutUsContent() {
        val aboutUsTitle = mAboutUsPage?.title?.renderedTitle
        if (aboutUsTitle != null && mAboutUsPage?.content != null) {
            val aboutUsContent = removeListIcons(mAboutUsPage?.content?.renderedContent!!)

            mAboutUsTitleTextView.text = aboutUsTitle

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mAboutUsContentTextView.text = Html.fromHtml(aboutUsContent, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mAboutUsContentTextView.text = Html.fromHtml(aboutUsContent)
            }
        }

        GlideApp.with(this)
                .load("http://mdmbaku.com/wp-content/uploads/2018/03/CompanyPresentationAchive.008.jpeg")
                .into(mPartnersLogosImageView)
    }

    private fun removeListIcons(aboutUsContent: String): String {

        return aboutUsContent.replace("<ul>", "")
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
        mSlider.stopAutoCycle()
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
