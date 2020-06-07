package cn.by.test.deeplink.adddeeplink

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import cn.by.test.deeplink.db.DeepLinkDatabase
import cn.by.test.deeplink.db.DeeplinkModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AddDeepLinkPresenter(
    private val delegate: AddDeepLinkDelegate,
    private val database: DeepLinkDatabase,
    lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    //region Variables
    private val viewModel: AddDeepLinkViewModel by lazy { delegate.getViewModel(AddDeepLinkViewModel::class.java) as AddDeepLinkViewModel }
    //endregion

    //region Lifecycle
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        initializeUrlText()
        initializeDescriptionText()
    }
    //endregion

    //region Listener
    fun onUrlTextChanged(url: String) {
        viewModel.deepLinkUrl = url
    }

    fun onDescriptionTextChanged(description: String) {
        viewModel.deepLinkDescription = description
    }

    fun onAddDeepLinkClick() {
        if (viewModel.deepLinkUrl.isNullOrEmpty()) {
            delegate.showDeepLinkEmptyToast()
        } else {
            addDeepLinkToDb(false)
        }
    }

    fun onAddAndFireDeepLinkClick() {
        if (viewModel.deepLinkUrl.isNullOrEmpty()) {
            delegate.showDeepLinkEmptyToast()
        } else {
            addDeepLinkToDb(true)
        }
    }
    //endregion

    //region Private Helpers
    private fun addDeepLinkToDb(shouldFire: Boolean) {
        viewModel.deepLinkUrl?.let {
            val host = it.split(":").first()
            val deeplink = DeeplinkModel(
                id = UUID.randomUUID().toString(),
                host = host,
                url = it,
                description = viewModel.deepLinkDescription
            )

            if (viewModel.addDeepLinkCompletable == null) {
                viewModel.addDeepLinkCompletable = Completable.fromAction {
                    database.deepLinkDao().insertDeepLink(deeplink)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.addDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    if (shouldFire) {
                        delegate.openDeepLinkActivity(it)
                    }
                    delegate.setResultOk()
                    delegate.finish()

                }
        }
    }

    private fun initializeUrlText() {
        if (viewModel.deepLinkUrl.isNullOrEmpty()) return
        delegate.updateUrlText(viewModel.deepLinkUrl)
    }

    private fun initializeDescriptionText() {
        if (viewModel.deepLinkDescription.isNullOrEmpty()) return
        delegate.updateDescriptionText(viewModel.deepLinkDescription)
    }
    //endregion
}