package com.ethan.ecgwave.view.kti

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import com.ethan.ecgwavekt.view.BaseDrawerKt
import com.ethan.ecgwavekt.view.ECGRealTimeChartKt

class GridDrawerKt: BaseDrawerKt {
    private val TAG = "BackgroundDrawer"

    private var lineColor: Int = Color.GREEN;

    private var pathEffect: PathEffect =  DashPathEffect(floatArrayOf(2f , 3f) , 0F);
    // 每小格的实际长度
    private var gridSpace = 30f;
    // 水平方向和垂直方向的线条数
    private var hLineCount = 0
    private var vLineCount = 0

    constructor(chartKt: ECGRealTimeChartKt){
        this.mBaseChart = chartKt
        linePaint = Paint()
        linePaint.color = lineColor
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.strokeWidth = 1f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onSizeChange() {
        super.onSizeChange()
        initBitmap()
    }

    override fun computeScroll() {

    }

    /**
     * @return 每小格边长
     */
    public fun getGridSpace(): Float{
        return this.gridSpace
    }

    public fun setGridSpace(space: Float){
        this.gridSpace = space
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    /**
     * 准备好画网格的 Bitmap
     */
    private fun initBitmap(){
        // 计算横线和竖线条数
        hLineCount = (viewHeight / gridSpace).toInt()
        vLineCount = (viewWidth / gridSpace).toInt()
        // 画横线
        for (h in 0 until hLineCount){
            var startX = 0f
            var startY = gridSpace * h
            var stopX = viewWidth
            var stopY = gridSpace * h
            // 每个 5根画一条粗线
            if (h % 5 != 0){
                linePaint.pathEffect = pathEffect
                linePaint.strokeWidth = 1.5f
            }else {
                linePaint.pathEffect = null
                linePaint.strokeWidth = 3f
            }
            // 画线
            bitmapCanvas.drawLine(startX, startY, stopX.toFloat(),stopY, linePaint);
        }
        // 画竖线
        for (v in 0 until vLineCount){
            val startX = gridSpace * v
            val startY = 0f;
            val stopX = gridSpace * v
            val stopY = viewHeight
            // 每隔 5根画一条竖线
            if (v % 5 != 0){
                linePaint.pathEffect = pathEffect
                linePaint.strokeWidth = 1.5f
            }else {
                linePaint.pathEffect = null
                linePaint.strokeWidth = 3f
                Log.d(TAG, "v = $v")
            }
            // 画线
            bitmapCanvas.drawLine(startX, startY, stopX, stopY.toFloat(), linePaint)
        }
    }
}