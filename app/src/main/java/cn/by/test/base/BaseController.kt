package cn.by.test.base

import android.app.Activity
import android.view.View
import com.bluelinelabs.conductor.Controller

abstract class BaseController : Controller() {

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        getPresenter().onActivityStarted()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        getPresenter().onAttach()
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        getPresenter().onActivityStopped(activity.isChangingConfigurations)
    }

    abstract fun getPresenter(): BasePresenter
}