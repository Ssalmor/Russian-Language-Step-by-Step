package com.langapp.russianlanguage_stepbystep.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.Selection
import android.text.Selection.getSelectionEnd
import android.text.Selection.getSelectionStart
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatEditText


class FillInBlanksEditText : AppCompatEditText, OnFocusChangeListener, TextWatcher {
    private var mLastSelStart = 0
    private var mLastSelEnd = 0
    private var mSpans: Array<BlanksSpan>? = null
    private var mUndoChange: Editable? = null
    private var mWatcherSpan: BlanksSpan? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mSpans = setSpans()
        onFocusChangeListener = this
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        mSpans = null
        super.onRestoreInstanceState(state)
        val e: Editable = editableText
        mSpans = e.getSpans(0, e.length, BlanksSpan::class.java)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            addTextChangedListener(this)
            if (findInSpan(selectionStart, selectionEnd) != null) {
                mLastSelStart = selectionStart
                mLastSelEnd = selectionEnd
            } else if (mSpans != null && mSpans!!.isEmpty().not() && findInSpan(mLastSelStart, mLastSelEnd) == null) {
                setSelection(editableText.getSpanStart(mSpans!![0]))
            }

        } else {
            removeTextChangedListener(this)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (!isFocused || mSpans == null || (selectionStart == mLastSelStart && selectionEnd == mLastSelEnd)) {
            return
        }

        val span = findInSpan(selStart, selEnd)
        if (span == null) {
            moveCursor(mLastSelStart)
        } else if (selStart > editableText.getSpanStart(span) + span.dataLength) {
            // Acceptable location for selection (within a Blankspan).
            // Make sure that the cursor is at the end of the entered data.  mLastSelStart = getEditableText().getSpanStart(span) + span.getDataLength();
            mLastSelEnd = mLastSelStart
            moveCursor(mLastSelStart)
        } else {
            mLastSelStart = selStart
            mLastSelEnd = selEnd
        }
        super.onSelectionChanged(mLastSelStart, mLastSelEnd)
    }

    private fun moveCursor(selStart: Int) {
        post { setSelection(selStart) }
        viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }

    private fun findInSpan(selStart: Int, selEnd: Int): BlanksSpan? {
        mSpans?.let {
            for (span in it) {
                if (selStart >= editableText.getSpanStart(span) &&
                    selEnd <= editableText.getSpanEnd(span)
                ) {
                    return span
                }
            }
        }
        return null
    }

    private fun setSpans(): Array<BlanksSpan> {
        val e: Editable = editableText
        val s = e.toString()
        var offset = 0
        var blanksOffset: Int
        while (s.substring(offset).indexOf(BLANKS_TOKEN).also {
                blanksOffset = it
            } != -1) {
            offset += blanksOffset
            e.setSpan(
                BlanksSpan(Typeface.BOLD), offset, offset + BLANKS_TOKEN.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            offset += BLANKS_TOKEN.length
        }
        return e.getSpans(0, e.length, BlanksSpan::class.java)
    }

    // Check change to make sure that it is acceptable to us.
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        mWatcherSpan = findInSpan(start, start + count)
        if (mWatcherSpan == null) {
            mUndoChange = Editable.Factory.getInstance().newEditable(s);
        } else {
            // Change is OK. Track data length.
            mWatcherSpan!!.adjustDataLength(count, after)
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // Do nothing...
    }

    override fun afterTextChanged(s: Editable) {
        if (mUndoChange == null) {
            val spanStart = s.getSpanStart(mWatcherSpan)
            val spanEnd = s.getSpanEnd(mWatcherSpan)
            if (spanStart in 0..spanEnd) { // Убедитесь, что spanStart не больше, чем spanEnd
                removeTextChangedListener(this)
                val selection: Int = selectionStart.coerceAtLeast(0).coerceAtMost(s.length) // Убеждаемся, что selection находится в пределах длины s

                // Получаем новое содержимое для спана
                val newContents = mWatcherSpan?.getFormattedContent(s)

                // Если newContents не null и не пусто, заменяем содержимое на newContents
                // В противном случае, если newContents пусто, вставляем BLANKS_TOKEN
                if (!newContents.isNullOrEmpty()) {
                    s.replace(spanStart, spanEnd, newContents)
                } else if (newContents.isNullOrEmpty() && spanStart == spanEnd) {
                    s.insert(spanStart, BLANKS_TOKEN) // Восстанавливаем BLANKS_TOKEN
                }

                setSelection(selection)
                addTextChangedListener(this)
            }
        } else {
            // Illegal change - put things back the way they were.
            removeTextChangedListener(this)
            text = mUndoChange
            mUndoChange = null
            addTextChangedListener(this)
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        mSpans = setSpans()
        //mSpans = Editable.Factory.getInstance().newEditable(text).getSpans(0, text!!.length, BlanksSpan::class.java)
    }

    @SuppressWarnings("WeakerAccess")
    class BlanksSpan : StyleSpan {
        var dataLength = 0
            private set

        constructor(style: Int) : super(style)

        @Suppress("unused")
        constructor(src: Parcel) : super(src)

        fun adjustDataLength(count: Int, after: Int) {
            dataLength += after - count
        }

        fun getFormattedContent(e: Editable): CharSequence? {
            val spanStart = e.getSpanStart(this)
            val spanEnd = e.getSpanEnd(this)
            if (spanStart == -1 || spanEnd == -1 || spanStart > spanEnd) {
                return null // Возвращаем null, если спаны не найдены или их индексы некорректны
            }
            val actualDataLength = minOf(dataLength, spanEnd - spanStart)
            return e.subSequence(spanStart, spanStart + actualDataLength)
        }

    }

    companion object {
        private const val BLANKS_TOKEN: String = "_____"
    }

}