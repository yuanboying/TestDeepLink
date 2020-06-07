package cn.by.test.deeplink.adddeeplink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import cn.by.test.R
import cn.by.test.base.BaseActivity
import cn.by.test.deeplink.db.DeepLinkDatabase
import cn.by.test.utils.TextChangedListener
import cn.by.test.utils.createClickListenerObservable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_add_deeplink.*

class AddDeepLinkActivity : BaseActivity(), AddDeepLinkDelegate {

    //region Variables
    private lateinit var presenter: AddDeepLinkPresenter
    private val compositeDisposable = CompositeDisposable()
    private val urlTextChangedListener = object : TextChangedListener() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            presenter.onUrlTextChanged(s.toString())
        }
    }

    private val descriptionChangedListener = object : TextChangedListener() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            presenter.onDescriptionTextChanged(s.toString())
        }
    }
    //endregion

    //region Intent
    companion object {
        fun createIntent(context: Context): Intent =
            Intent(context, AddDeepLinkActivity::class.java)
    }
    //endregion

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AddDeepLinkPresenter(this, DeepLinkDatabase.get(this), this)
    }

    override fun initializeView() {
        compositeDisposable.addAll(
            addDeepLinkButton.createClickListenerObservable().subscribe { presenter.onAddDeepLinkClick() },
            addAndFireDeepLinkButton.createClickListenerObservable().subscribe { presenter.onAddAndFireDeepLinkClick() }
        )

        addDeepLinkUrl.addTextChangedListener(urlTextChangedListener)
        addDeepLinkDescription.addTextChangedListener(descriptionChangedListener)
    }

    override fun getLayoutId(): Int = R.layout.activity_add_deeplink

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
    //endregion

    //region Delegate Implementation
    override fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel =
        ViewModelProviders.of(this as FragmentActivity).get(clazz)

    override fun showDeepLinkEmptyToast() {

    }

    override fun updateUrlText(url: String?) {
        addDeepLinkUrl.removeTextChangedListener(urlTextChangedListener)
        addDeepLinkUrl.setText(url)
        addDeepLinkUrl.addTextChangedListener(urlTextChangedListener)
    }

    override fun updateDescriptionText(description: String?) {
        addDeepLinkDescription.removeTextChangedListener(descriptionChangedListener)
        addDeepLinkDescription.setText(description)
        addDeepLinkDescription.addTextChangedListener(descriptionChangedListener)
    }

    override fun setResultOk() {
        setResult(Activity.RESULT_OK)
    }

    override fun openDeepLinkActivity(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        startActivity(intent)
    }
    //endregion
}