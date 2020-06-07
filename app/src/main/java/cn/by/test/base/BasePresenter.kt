package cn.by.test.base

abstract class BasePresenter {

    private var isChangingConfigurations = false
    private var isStopped = false
    private var hasBeenAttached = false

    fun onActivityStarted() {
        if (isStopped && !isChangingConfigurations) {
            onAttach()
        }
    }

    fun onActivityStopped(isChangingConfigurations: Boolean) {
        this.isChangingConfigurations = isChangingConfigurations
        this.isStopped = true
    }

    fun onAttach() {
        if (isReattach()) {
            onReattach()
        } else {
            onInitialAttach()
        }
        hasBeenAttached = true
    }

    private fun isReattach(): Boolean = !isChangingConfigurations && hasBeenAttached

    abstract fun onReattach()

    abstract fun onInitialAttach()
}