package cn.by.test.base

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.by.test.R

abstract class BaseActivity : AppCompatActivity() {

    //region Variables
    internal var bundle: Bundle? = null
    //endregion

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        updateStatusBarStyle()
        bundle = savedInstanceState
        initializeView()
    }
    //endregion

    //region Abstract
    abstract fun getLayoutId(): Int

    abstract fun initializeView()
    //endregion

    //region Private Helpers
    private fun updateStatusBarStyle() {
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            val flags = decorView.systemUiVisibility
            decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.background_home)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.background_home)
        }
    }
    //endregion
}