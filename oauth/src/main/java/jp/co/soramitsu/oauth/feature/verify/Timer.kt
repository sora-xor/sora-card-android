package jp.co.soramitsu.oauth.feature.verify

import android.os.CountDownTimer

private const val RESENT_OTP_MAX_TIME_SECONDS = 60
private const val SECOND_IN_MILLS = 1000L

class Timer(
    millisInFuture: Long = RESENT_OTP_MAX_TIME_SECONDS * SECOND_IN_MILLS,
    countDownInterval: Long = SECOND_IN_MILLS,
) : CountDownTimer(millisInFuture, countDownInterval) {

    private var onTickListener: ((Long) -> Unit)? = null
    private var onFinishListener: (() -> Unit)? = null

    override fun onTick(millisUntilFinished: Long) {
        onTickListener?.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        onFinishListener?.invoke()
    }

    fun setOnTickListener(onTickListener: (Long) -> Unit) {
        this.onTickListener = onTickListener
    }

    fun setOnFinishListener(onFinishListener: () -> Unit) {
        this.onFinishListener = onFinishListener
    }
}
