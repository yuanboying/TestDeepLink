package cn.by.test.deeplink

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.by.test.deeplink.db.DeeplinkModel
import cn.by.test.deeplink.deeplinkrow.DeepLinkRow
import cn.by.test.utils.createClickListenerObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.ArrayList

class DeepLinkListAdapter constructor(
    private val context: Context,
    private var listener: Listener?
) : RecyclerView.Adapter<DeepLinkListAdapter.DeepLinkRowViewHolder>() {

    //region Listener
    interface Listener {
        fun onDeepLinkRowClick(url: String?)
    }
    //endregion

    //region Variables
    private var deepLinkList: ArrayList<DeeplinkModel>? = null
    private val compositeDisposable = CompositeDisposable()
    private var searchText: String? = null
    //endregion

    //region ViewHolder
    class DeepLinkRowViewHolder(deepLinkRow: DeepLinkRow) : RecyclerView.ViewHolder(deepLinkRow)
    //endregion

    //region Implements
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeepLinkRowViewHolder {
        val view = DeepLinkRow(context)
        return DeepLinkRowViewHolder(view)
    }

    override fun getItemCount(): Int = deepLinkList?.size ?: 0

    override fun onBindViewHolder(holder: DeepLinkRowViewHolder, position: Int) {
        (holder.itemView as? DeepLinkRow)?.apply {
            updateDeepLinkInfo(deepLinkList?.get(position), searchText)
            createClickListenerObservable().subscribe {
                listener?.onDeepLinkRowClick(
                    deepLinkList?.get(
                        position
                    )?.url
                )
            }.addTo(compositeDisposable)
        }
    }
    //endregion

    //region external operations
    fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?, searchText: String? = null) {
        this.deepLinkList = deepLinkList as ArrayList<DeeplinkModel>
        this.searchText = searchText
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        deepLinkList?.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(position: Int, deeplinkModel: DeeplinkModel) {
        deepLinkList?.add(position, deeplinkModel)
        notifyItemInserted(position)
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        listener = null
    }
    //endregion
}