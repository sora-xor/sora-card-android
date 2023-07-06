package jp.co.soramitsu.oauth.common.domain

import android.app.Activity

interface CurrentActivityRetriever {

    fun setActivity(activity: Activity)

    fun getCurrentActivity(): Activity

}