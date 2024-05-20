package com.goalmaster.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ProgressBar

class DoublerProgressBar : ProgressBar {
    private var textPaint: Paint

    constructor(context: Context) : super(context) {
        textPaint = Paint()
        textPaint.color = Color.BLACK
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        textPaint = Paint()
        textPaint.color = Color.BLACK
        max = 100
    }

}