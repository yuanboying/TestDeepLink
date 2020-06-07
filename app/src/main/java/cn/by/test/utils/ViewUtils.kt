package cn.by.test.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ViewUtils {
}


fun View?.goneView() {
    this?.visibility = View.GONE
}

fun View?.visibleView() {
    this?.visibility = View.VISIBLE
}

fun View.createClickListenerObservable(): Observable<Unit?> {
    return this.clicks().throttleFirst(200L, TimeUnit.MILLISECONDS)
}