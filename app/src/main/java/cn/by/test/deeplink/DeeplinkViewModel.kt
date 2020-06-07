package cn.by.test.deeplink

import androidx.lifecycle.ViewModel
import cn.by.test.deeplink.db.DeeplinkModel
import io.reactivex.Completable
import io.reactivex.Single
import java.util.ArrayList

class DeeplinkViewModel : ViewModel() {
    var deeplinkList: ArrayList<DeeplinkModel>? = null
    var getDeepLinkListSingle: Single<List<DeeplinkModel>>? = null
    var swipedItemPosition: Int = 0
    var swipedItem: DeeplinkModel? = null
    var isDeleteUndone = false
    var searchText: String? = null
    var isSnackbarShowing = false

    var deleteDeepLinkCompletable: Completable? = null
}