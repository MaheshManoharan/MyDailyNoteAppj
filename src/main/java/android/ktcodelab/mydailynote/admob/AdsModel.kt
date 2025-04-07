package android.ktcodelab.mydailynote.admob

data class AdsModel(
    val id: String,
    val banner_id: String,
    val appopen_id: String,
    val interstitial_id: String,
    val native_id: String,
    val ad_status: Boolean
) {

    constructor(): this("", "", "", "", "", false)
}
