package com.technoapps.vehiclemanager.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AdsIdDataResponse {

    constructor(statusCode:Int)
    {
        this.statusCode = statusCode
    }

    @SerializedName("status_code")
    @Expose
    internal var statusCode: Int? = null


    @SerializedName("id")
    @Expose
    internal var id: Int? = null

    @SerializedName("advertisement_type")
    @Expose
    internal var advertisementType: String? = null

    @SerializedName("advertise_key")
    @Expose
    internal var advertiseKey: String? = null

    @SerializedName("banner_key")
    @Expose
    internal var bannerKey: String? = null

    @SerializedName("interstitial_key")
    @Expose
    internal var interstitialKey: String? = null

    @SerializedName("rewarded_video")
    @Expose
    internal var rewardedVideo: String? = null

    @SerializedName("native_advanced")
    @Expose
    internal var nativeAdvanced: String? = null

    @SerializedName("facebook_advertise")
    @Expose
    internal var facebookAdvertise: String? = null

    @SerializedName("facebook_banner")
    @Expose
    internal var facebookBanner: String? = null

    @SerializedName("facebook_interstitial")
    @Expose
    internal var facebookInterstitial: String? = null

    @SerializedName("facebook_native")
    @Expose
    internal var facebookNative: String? = null

    @SerializedName("facebook_native_banner")
    @Expose
    internal var facebookNativeBanner: String? = null

    @SerializedName("facebook_rewarded_video")
    @Expose
    internal var facebookRewardedVideo: String? = null

    @SerializedName("facebook_medium_rectangle")
    @Expose
    internal var facebookMediumRectangle: String? = null

    @SerializedName("version_code")
    @Expose
    internal var versionCode: Int? = null

    @SerializedName("force_update")
    @Expose
    internal var forceUpdate: Int? = null

    @SerializedName("first_click_count")
    @Expose
    internal var firstClickCount: Int? = null

    @SerializedName("second_click_count")
    @Expose
    internal var secondClickCount: Int? = null

    @SerializedName("created_at")
    @Expose
    internal var createdAt: String? = null

    @SerializedName("status")
    @Expose
    internal var status: String? = null

    @SerializedName("application")
    @Expose
    internal var application: Int? = null

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getAdvertisementType(): String? {
        return advertisementType
    }

    fun setAdvertisementType(advertisementType: String?) {
        this.advertisementType = advertisementType
    }

    fun getAdvertiseKey(): String? {
        return advertiseKey
    }

    fun setAdvertiseKey(advertiseKey: String?) {
        this.advertiseKey = advertiseKey
    }

    fun getBannerKey(): String? {
        return bannerKey
    }

    fun setBannerKey(bannerKey: String?) {
        this.bannerKey = bannerKey
    }

    fun getInterstitialKey(): String? {
        return interstitialKey
    }

    fun setInterstitialKey(interstitialKey: String?) {
        this.interstitialKey = interstitialKey
    }

    fun getRewardedVideo(): String? {
        return rewardedVideo
    }

    fun setRewardedVideo(rewardedVideo: String?) {
        this.rewardedVideo = rewardedVideo
    }

    fun getNativeAdvanced(): String? {
        return nativeAdvanced
    }

    fun setNativeAdvanced(nativeAdvanced: String?) {
        this.nativeAdvanced = nativeAdvanced
    }

    fun getFacebookAdvertise(): String? {
        return facebookAdvertise
    }

    fun setFacebookAdvertise(facebookAdvertise: String?) {
        this.facebookAdvertise = facebookAdvertise
    }

    fun getFacebookBanner(): String? {
        return facebookBanner
    }

    fun setFacebookBanner(facebookBanner: String?) {
        this.facebookBanner = facebookBanner
    }

    fun getFacebookInterstitial(): String? {
        return facebookInterstitial
    }

    fun setFacebookInterstitial(facebookInterstitial: String?) {
        this.facebookInterstitial = facebookInterstitial
    }

    fun getFacebookNative(): String? {
        return facebookNative
    }

    fun setFacebookNative(facebookNative: String?) {
        this.facebookNative = facebookNative
    }

    fun getFacebookNativeBanner(): String? {
        return facebookNativeBanner
    }

    fun setFacebookNativeBanner(facebookNativeBanner: String?) {
        this.facebookNativeBanner = facebookNativeBanner
    }

    fun getFacebookRewardedVideo(): String? {
        return facebookRewardedVideo
    }

    fun setFacebookRewardedVideo(facebookRewardedVideo: String?) {
        this.facebookRewardedVideo = facebookRewardedVideo
    }

    fun getFacebookMediumRectangle(): String? {
        return facebookMediumRectangle
    }

    fun setFacebookMediumRectangle(facebookMediumRectangle: String?) {
        this.facebookMediumRectangle = facebookMediumRectangle
    }

    fun getVersionCode(): Int? {
        return versionCode
    }

    fun setVersionCode(versionCode: Int?) {
        this.versionCode = versionCode
    }

    fun getForceUpdate(): Int? {
        return forceUpdate
    }

    fun setForceUpdate(forceUpdate: Int?) {
        this.forceUpdate = forceUpdate
    }

    fun getFirstClickCount(): Int? {
        return firstClickCount
    }

    fun setFirstClickCount(firstClickCount: Int?) {
        this.firstClickCount = firstClickCount
    }

    fun getSecondClickCount(): Int? {
        return secondClickCount
    }

    fun setSecondClickCount(secondClickCount: Int?) {
        this.secondClickCount = secondClickCount
    }

    fun getCreatedAt(): String? {
        return createdAt
    }

    fun setCreatedAt(createdAt: String?) {
        this.createdAt = createdAt
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getApplication(): Int? {
        return application
    }

    fun setApplication(application: Int?) {
        this.application = application
    }

    fun isForceFullyUpdate(): Boolean? {
        return forceUpdate == 1
    }


}