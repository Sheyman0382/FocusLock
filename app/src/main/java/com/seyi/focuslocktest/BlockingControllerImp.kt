package com.seyi.focuslocktest

class BlockingControllerImpl(
    //private val appBlocker: AppBlocker,
    //private val websiteBlocker: WebsiteBlocker
) : BlockingController {

    override fun startBlocking() {
        //appBlocker.startBlocking()
        //websiteBlocker.startBlocking()
        AppBlockerImpl().startBlocking()
        WebsiteBlockerImpl().startBlocking()
    }

    override fun stopBlocking() {
        //appBlocker.stopBlocking()
        //websiteBlocker.stopBlocking()

        AppBlockerImpl().stopBlocking()
        WebsiteBlockerImpl().stopBlocking()
    }
}