package android.ktcodelab.mydailynote.admob

import android.content.Context
import android.ktcodelab.mydailynote.R
import android.ktcodelab.mydailynote.util.findActivity
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

var adsModel = AdsModel()

@Composable
fun AdMobAds() {

    val firebaseFireStore = Firebase.firestore
    val currentWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

    /*-----------------------------Fetch Data From Firebase------------------------------*/

    val docRef = firebaseFireStore
        .collection(context.getString(R.string.firebase_collection))
        .document(context.getString(R.string.firebase_document_ID))

    docRef.addSnapshotListener(EventListener { value, error ->

        if (error != null) {
            Log.d("TAG", "Error: ${error.message.toString()}")

        } else {
            adsModel = value?.toObject(AdsModel::class.java)!!
        }
    })

    /*-----------------------------Banner Ads------------------------------*/
    if (adsModel.ad_status){

        AndroidView(
            factory = {
                AdView(it).apply {

                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, currentWidth))
                    //adUnitId = context.getString(R.string.banner_ad_id) //From String
                    adUnitId = adsModel.banner_id //from Server
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
/*-----------------------------InterstitialAd------------------------------*/
var mInterstitialAd: InterstitialAd? = null

//Load interstitial Ads
fun loadInterstitial(context: Context){

    InterstitialAd.load(
        context,
        adsModel.interstitial_id,
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback(){

            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                mInterstitialAd = interstitialAd
                Log.d("TAG", "Ad was loaded.")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {

                mInterstitialAd = null
                Log.d("TAG", adError.message)
            }
        }
    )
}

// Add the interstitial ad callbacks
fun addInterstitialCallbacks(context: Context){
    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()

            mInterstitialAd = null
            loadInterstitial(context)

            Log.d("TAG", "Ad was dismissed.")
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)

            mInterstitialAd = null

            Log.d("TAG", "Ad failed to show.")
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()

            Log.d("TAG", "Ad showed fullscreen content.")
        }
    }
}

// Show the interstitial ad
fun showInterstitial(context: Context){

    if (adsModel.ad_status){

        val activity = context.findActivity()

        if (mInterstitialAd != null){

            addInterstitialCallbacks(context = context)

            mInterstitialAd?.show(activity!!)

        } else {
            loadInterstitial(context = context)

            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
}


















