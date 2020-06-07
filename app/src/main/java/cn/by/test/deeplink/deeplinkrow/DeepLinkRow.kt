package cn.by.test.deeplink.deeplinkrow

import android.content.Context
import android.content.res.ColorStateList
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import cn.by.test.R
import cn.by.test.deeplink.db.DeeplinkModel
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.TextAppearanceSpan
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.component_deep_link_row.view.*
import java.util.*

class DeepLinkRow @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle), LifecycleObserver {

    //region Init
    init {
        LayoutInflater.from(context).inflate(R.layout.component_deep_link_row, this, true)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    //endregion

    //region Update
    fun updateDeepLinkInfo(deeplinkModel: DeeplinkModel?, filter: String? = null) {
        updateTextWithHighlight(deepLinkUrl, deeplinkModel?.url, filter)
        updateTextWithHighlight(deepLinkDescription, deeplinkModel?.description, filter)
    }

    private fun updateTextWithHighlight(textView: TextView, text: String?, filter: String?) {
        if (filter.isNullOrBlank()) {
            textView.text = text
        } else {
            val startPosition =
                text?.toLowerCase(Locale.US)?.indexOf(filter.toLowerCase(Locale.US)) ?: -1
            val endPosition = startPosition + filter.length
            if (startPosition != -1) {
                val spannable = SpannableString(text)
                val blueColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.BLUE))
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null)
                spannable.setSpan(
                    highlightSpan,
                    startPosition,
                    endPosition,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textView.text = spannable
            } else {
                textView.text = text
            }
        }
    }
    //endregion
}