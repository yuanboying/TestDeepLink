package cn.by.test.utils

import android.text.Editable
import android.text.TextWatcher

abstract class TextChangedListener : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}