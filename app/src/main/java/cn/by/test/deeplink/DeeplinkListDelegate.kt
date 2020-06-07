package cn.by.test.deeplink

import androidx.lifecycle.ViewModel
import cn.by.test.deeplink.db.DeeplinkModel

interface DeeplinkListDelegate {
    fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel
    fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?, searchText: String? = null)
    fun openAddDeepLinkActivity(requestCode: Int)
    fun showDeepLinkDeleteToast()
    fun dismissSnackbar()
    fun showSnackbar()
    fun removeSnackbarCallback()
    fun addSnackbarCallback()
    fun removeItem(position: Int)
    fun updateSearchText(searchText: String?)
    fun restoreItem(position: Int, deeplinkModel: DeeplinkModel)
    fun showNoDeepLinkToast(toast: String)
    fun openDeepLinkActivity(url: String?)
}