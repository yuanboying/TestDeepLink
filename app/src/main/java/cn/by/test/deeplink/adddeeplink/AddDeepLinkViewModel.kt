package cn.by.test.deeplink.adddeeplink

import androidx.lifecycle.ViewModel
import io.reactivex.Completable

class AddDeepLinkViewModel : ViewModel() {
    var deepLinkUrl: String? = null
    var deepLinkDescription: String? = null
    var addDeepLinkCompletable: Completable? = null
}