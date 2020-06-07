package cn.by.test.deeplink

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.util.ArrayMap
import cn.by.test.base.BasePresenter
import cn.by.test.deeplink.db.DeepLinkDatabase
import cn.by.test.deeplink.db.DeeplinkModel
import cn.by.test.utils.RequestCode
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class DeeplinkListPresenter constructor(
    private val delegate: DeeplinkListDelegate,
    private val deepLinkDatabase: DeepLinkDatabase
) : BasePresenter() {

    //region Variables
    private val viewModel: DeeplinkViewModel by lazy { delegate.getViewModel(DeeplinkViewModel::class.java) as DeeplinkViewModel }
    private var disposable: Disposable? = null
    private val indexMapFromFilteredListToOriginalList = ArrayMap<Int, Int>()
    private var filteredList: ArrayList<DeeplinkModel>? = null
    //endregion

    //region Lifecycle
    override fun onReattach() {

    }

    override fun onInitialAttach() {
        getDeepLinkListFromDb()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == RequestCode.ADD_DEEP_LINK && resultCode == RESULT_OK) {
            refreshDeepLinkList()
        }
    }

    fun onDestroy() {
        onSnackbarDismissed()
    }
    //endregion

    //region Clicks
    fun onAddDeepLinkClick() {
        delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
    }

    fun onItemSwiped(position: Int) {
        viewModel.swipedItemPosition = position
        if (viewModel.swipedItem != null) {
            deletePreviousDeepLinkFromDb()
        } else {
            viewModel.swipedItem = filteredList?.get(position)
            removeDeepLink()
        }
    }

    fun onUndoClick() {
        viewModel.swipedItem?.let {
            viewModel.deeplinkList?.add(
                indexMapFromFilteredListToOriginalList[viewModel.swipedItemPosition] ?: 0, it
            )
            delegate.restoreItem(viewModel.swipedItemPosition, it)
        }

        viewModel.isDeleteUndone = true
    }

    fun onSnackbarDismissed() {
        if (viewModel.isSnackbarShowing) {
            if (!viewModel.isDeleteUndone) {
                deleteDeepLinkFromDb()
            } else {
                viewModel.isDeleteUndone = false
            }
        }
        viewModel.isSnackbarShowing = false
    }

    fun onDeepLinkRowClick(url: String?) {
        try {
            delegate.openDeepLinkActivity(url)
        } catch (e: ActivityNotFoundException) {
            delegate.showNoDeepLinkToast(e.message ?: "")
        }
    }

    // search bar will restore the search text itself and call its text watcher
    fun onSearchTextChanged(searchText: String?) {
        viewModel.searchText = searchText
        updateDeepLinkList()
    }
    //endregion

    //region Private Helpers
    private fun deleteDeepLinkFromDb() {
        viewModel.swipedItem?.let {
            if (viewModel.deleteDeepLinkCompletable == null) {
                viewModel.deleteDeepLinkCompletable = Completable.fromAction {
                    deepLinkDatabase.deepLinkDao().deleteDeepLink(it)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.deleteDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    viewModel.deleteDeepLinkCompletable = null
                    viewModel.swipedItem = null
                    delegate.showDeepLinkDeleteToast()
                }
        }
    }

    private fun deletePreviousDeepLinkFromDb() {
        viewModel.swipedItem?.let {
            if (viewModel.deleteDeepLinkCompletable == null) {
                viewModel.deleteDeepLinkCompletable = Completable.fromAction {
                    deepLinkDatabase.deepLinkDao().deleteDeepLink(it)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.deleteDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    viewModel.deleteDeepLinkCompletable = null
                    viewModel.swipedItem = filteredList?.get(viewModel.swipedItemPosition)
                    removeDeepLink()
                }
        }
    }

    private fun getDeepLinkListFromDb() {
        if (viewModel.deeplinkList.isNullOrEmpty() && viewModel.searchText.isNullOrBlank()) {
            if (viewModel.getDeepLinkListSingle == null) {
                viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
            }

            disposable = viewModel.getDeepLinkListSingle
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    {
                        viewModel.deeplinkList = it as ArrayList
                        if (it.isEmpty()) {
                            delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
                        } else {
                            updateDeepLinkList()
                        }

                        disposable?.dispose()
                    },
                    {
                        disposable?.dispose()
                    }
                )
        }
    }

    private fun refreshDeepLinkList() {
        if (viewModel.getDeepLinkListSingle == null) {
            viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
        }

        disposable = viewModel.getDeepLinkListSingle
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                {
                    viewModel.deeplinkList = it as ArrayList
                    delegate.updateSearchText(null)
                    disposable?.dispose()
                },
                {
                    disposable?.dispose()
                }
            )
    }

    private fun getDeepLinksFromDb(): Single<List<DeeplinkModel>> =
        deepLinkDatabase.deepLinkDao()
            .getDeepLinkList()
            .subscribeOn(Schedulers.io())


    private fun updateDeepLinkList() {
        viewModel.deeplinkList?.apply {
            filteredList = this.filter {
                it.url.contains(
                    viewModel.searchText ?: "",
                    ignoreCase = true
                ) || it.description?.contains(viewModel.searchText ?: "") == true
            } as ArrayList
            updateIndexMapFromFilteredListToOriginalList()
            delegate.updateDeepLinkList(filteredList, viewModel.searchText)
        }
    }

    private fun updateIndexMapFromFilteredListToOriginalList() {
        indexMapFromFilteredListToOriginalList.clear()
        filteredList?.forEach {
            if (viewModel.deeplinkList?.contains(it) == true) {
                indexMapFromFilteredListToOriginalList[filteredList?.indexOf(it)] =
                    viewModel.deeplinkList?.indexOf(it)
            }
        }
    }

    private fun removeDeepLink() {
        viewModel.deeplinkList?.removeAt(
            indexMapFromFilteredListToOriginalList[viewModel.swipedItemPosition] ?: 0
        )
        delegate.removeItem(viewModel.swipedItemPosition)
        updateIndexMapFromFilteredListToOriginalList()
        delegate.showSnackbar()
        viewModel.isSnackbarShowing = true
    }
    //endregion
}