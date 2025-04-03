package jp.co.soramitsu.oauth.common.data

import android.app.Activity
import android.os.Looper
import java.lang.ref.WeakReference
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever

class CurrentActivityRetrieverImpl : CurrentActivityRetriever {

    private var activityWeakRef: WeakReference<Activity>? = null

    override fun setActivity(activity: Activity) {
        if (!Looper.getMainLooper().isCurrentThread) {
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS)
        }

        activityWeakRef = WeakReference(activity)
    }

    override fun getCurrentActivity(): Activity {
        if (!Looper.getMainLooper().isCurrentThread) {
            throw IllegalAccessError(NOT_MAIN_THREAD_ACCESS)
        }

        return activityWeakRef?.get()
            ?: throw IllegalStateException(NULL_ACTIVITY_ACCESS)
    }

    private companion object {
        const val NOT_MAIN_THREAD_ACCESS =
            "Access to WeakRef<Activity> is allowed only from one thread for safe memory leaklessness"

        const val NULL_ACTIVITY_ACCESS =
            "WeakRef<Activity> was either garbage collected or not set"
    }
}
