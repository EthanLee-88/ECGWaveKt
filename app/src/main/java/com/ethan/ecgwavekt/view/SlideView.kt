package com.ethan.ecgwavekt.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SlideView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val TAG = "Slide"
    }

    private val paint = Paint()
    private val path = Path()
    private val rect = RectF()
    private val paintStrokeWidth = 8F
    private var downX = 0
    private var offsetX = 0
    var verifySuccess = false
        set(value) {
            field = value
            downX = 0
            offsetX = 0
            invalidate()
        }

    init {
        paint.color = 0xFF00796B.toInt()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = paintStrokeWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (verifySuccess) {
            return false
        }
        val r = (height - 3 * paintStrokeWidth) / 2
        val startX = (1.5 * paintStrokeWidth + r).toFloat()
        if ((event?.pointerCount != 1) || ((event.action == MotionEvent.ACTION_DOWN) && (event.x > startX + r))) {
            offsetX = 0
            invalidate()
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x.toInt()
                offsetX = 0
            }
            MotionEvent.ACTION_MOVE -> {
                offsetX = (event.x - downX).toInt()
            }
            else -> {
                offsetX = 0
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        drawEllipse(canvas)
        drawCircle(canvas)
    }

    private fun drawEllipse(canvas: Canvas) {
        path.reset()
        path.addRoundRect(
            rect,
            500f,
            500f,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint)
    }

    private fun drawCircle(canvas: Canvas) {
        val r = (height - 3 * paintStrokeWidth) / 2
        val startX = (1.5 * paintStrokeWidth + r).toFloat()
        val endX = width - startX
        if (verifySuccess) {
            canvas.drawCircle(
                endX,
                (height / 2).toFloat(),
                r,
                paint
            )
            return
        }
        var rx = startX + offsetX
        if (rx >= endX) {
            verifySuccess = true
            return
        }
        if (rx < startX) {
            rx = startX
        }
        canvas.drawCircle(
            rx,
            (height / 2).toFloat(),
            r,
            paint
        )
    }
}