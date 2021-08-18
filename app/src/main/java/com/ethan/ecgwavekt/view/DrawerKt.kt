package com.ethan.ecgwave.view.kti

import android.graphics.Canvas
import android.view.MotionEvent

abstract class DrawerKt {
    abstract fun onDraw(canvas: Canvas)
    abstract fun onSizeChange()
    abstract fun onTouchEvent(event: MotionEvent): Boolean
    abstract fun computeScroll()
}