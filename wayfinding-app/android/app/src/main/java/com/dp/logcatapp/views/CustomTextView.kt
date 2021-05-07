package com.dp.logcatapp.views

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import ie.tcd.cs7cs3.wayfinding.R

private val typefaceCache = mutableMapOf<String, Typeface>()
fun Context.getTypeface(name: String): Typeface? {
    val assetPath = "fonts/$name.ttf"
    var typeface = typefaceCache[assetPath]
    if (typeface == null) {
        typeface = Typeface.createFromAsset(assets, assetPath)
        typefaceCache[assetPath] = typeface
    }
    return typeface
}

class CustomTextView : AppCompatTextView {

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        handleUseFont(context, attributeSet, 0)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {
        handleUseFont(context, attributeSet, defStyleAttr)
    }

    private fun handleUseFont(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(attributeSet,
                R.styleable.CustomTextView, defStyleAttr, 0)
        try {
            val fontName = typedArray.getString(R.styleable.CustomTextView_useFont)
            if (fontName != null) {
                typeface = context.getTypeface(fontName)
                paintFlags = paintFlags.or(Paint.SUBPIXEL_TEXT_FLAG)
            }
        } finally {
            typedArray.recycle()
        }
    }
}
