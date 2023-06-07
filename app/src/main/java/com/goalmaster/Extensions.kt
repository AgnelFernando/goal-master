package com.goalmaster

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {

    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

fun Date?.relativeDateFormat(context: Context): String {
    if (this == null) return context.getString(R.string.never)

    val thenMillis: Long = this.time
    if (thenMillis == 0L) {
        return context.getString(R.string.never)
    }
    val nowMillis = System.currentTimeMillis()
    return if (nowMillis - thenMillis < DateUtils.MINUTE_IN_MILLIS) {
        context.getString(R.string.just_now)
    } else DateUtils.getRelativeTimeSpanString(
        thenMillis, nowMillis,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}