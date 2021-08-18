package com.ethan.ecgwave.view.kti

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP

abstract class BaseDrawerKt: DrawerKt() {
    private val TAG = "BaseDrawer";

    protected lateinit var mBaseChart: ECGRealTimeChartKt
    protected lateinit var linePaint: Paint
    // 画 Bitmap
    protected lateinit var gridBitmap: Bitmap
    // 画 Canvas
    protected lateinit var bitmapCanvas: Canvas
    // 控件宽高
    protected var viewWidth = 0
    protected var viewHeight = 0

    override fun onSizeChange() {
        viewWidth = mBaseChart.width
        viewHeight = mBaseChart.height

        gridBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = Canvas(gridBitmap);
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(gridBitmap, mBaseChart.scrollX.toFloat(), 0f, null)
    }

    protected fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(COMPLEX_UNIT_SP, sp, mBaseChart.getResources().getDisplayMetrics());
    }

}