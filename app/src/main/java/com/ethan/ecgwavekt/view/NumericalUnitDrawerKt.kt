package com.ethan.scanview.view.kti

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import com.ethan.ecgwavekt.view.BaseDrawerKt
import com.ethan.ecgwavekt.view.ECGRealTimeChartKt

class NumericalUnitDrawerKt: BaseDrawerKt {
    private var lineColor = Color.RED

    constructor(chartKt: ECGRealTimeChartKt){
        this.mBaseChart = chartKt
        linePaint = Paint()
        linePaint.color = lineColor
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.strokeWidth = 2f
        linePaint.textSize = spToPx(12f)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun computeScroll() {

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText("Y : " + mBaseChart.getMvPerLargeGrid() + "mv / 大格",
            30f + mBaseChart.scrollX, 90f, linePaint)
        canvas.drawText("X : " + mBaseChart.getDataNumbersPerGrid() + "点 / 大格",
            30f + mBaseChart.scrollX, 60f, linePaint)
    }
}