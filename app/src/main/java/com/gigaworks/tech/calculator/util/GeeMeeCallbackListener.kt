package com.gigaworks.tech.calculator.util

import ai.geemee.GError
import ai.geemee.GeeMeeCallback

interface GeeMeeCallbackListener: GeeMeeCallback {

    private fun getClassName(): String {
        return "GeeMeeCallbackListener"
    }

    override fun onInitSuccess() {
        printLogD(this.getClassName(), "onInitSuccess")
    }

    override fun onInitFailed(p0: GError?) {
        printLogD(this.getClassName(), "onInitFailed")
    }

    override fun onBannerReady(p0: String?) {
        printLogD(this.getClassName(), "onBannerReady")
    }

    override fun onBannerLoadFailed(p0: String?, p1: GError?) {
        printLogD(this.getClassName(), "onBannerLoadFailed")
    }

    override fun onBannerShowFailed(p0: String?, p1: GError?) {
        printLogD(this.getClassName(), "onBannerShowFailed")
    }

    override fun onBannerClick(p0: String?) {
        printLogD(this.getClassName(), "onBannerClick")
    }

    override fun onInterstitialOpen(p0: String?) {
        printLogD(this.getClassName(), "onInterstitialOpen")
    }

    override fun onInterstitialOpenFailed(p0: String?, p1: GError?) {
        printLogD(this.getClassName(), "onInterstitialOpenFailed")
    }

    override fun onInterstitialClose(p0: String?) {
        printLogD(this.getClassName(), "onInterstitialClose")
    }

    override fun onOfferWallOpen(p0: String?) {
        printLogD(this.getClassName(), "onOfferWallOpen")
    }

    override fun onOfferWallOpenFailed(p0: String?, p1: GError?) {
        printLogD(this.getClassName(), "onOfferWallOpenFailed")
    }

    override fun onOfferWallClose(p0: String?) {
        printLogD(this.getClassName(), "onOfferWallClose")
    }

    override fun onUserCenterOpen(p0: String?) {
        printLogD(this.getClassName(), "onUserCenterOpen")
    }

    override fun onUserCenterOpenFailed(p0: String?, p1: GError?) {
        printLogD(this.getClassName(), "onUserCenterOpenFailed")
    }

    override fun onUserCenterClose(p0: String?) {
        printLogD(this.getClassName(), "onUserCenterClose")
    }

    override fun onUserInteraction(p0: String?, p1: String?) {
        printLogD(this.getClassName(), "onUserInteraction")
    }

}