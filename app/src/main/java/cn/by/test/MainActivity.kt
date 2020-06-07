package cn.by.test

import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import cn.by.test.base.BaseActivity
import cn.by.test.deeplink.DeeplinkListController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainDelegate {

    //region Variables
    private lateinit var presenter: MainPresenter
    private var router: Router? = null
    //endregion

    //region Lifecycle
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initializeView() {
        initializeRouter(bundle)
    }
    //endregion

    //region Delegate
    override fun placeHolder() {

    }
    //endregion

    //region Private Helpers
    private fun initializeRouter(savedInstanceState: Bundle?) {
        router = Conductor.attachRouter(this, contentLayout, savedInstanceState)

        router?.run {
            if (!hasRootController()) {
                setRoot(RouterTransaction.with(DeeplinkListController()))
            }
        }
    }
    //endregion
}