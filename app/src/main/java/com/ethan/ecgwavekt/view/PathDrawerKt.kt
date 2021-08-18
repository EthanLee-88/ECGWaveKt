package com.ethan.scanview.view.kti

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.Scroller
import com.ethan.ecgwave.view.kti.BaseDrawerKt
import com.ethan.ecgwave.view.kti.ECGRealTimeChartKt
import java.util.concurrent.CopyOnWriteArrayList

class PathDrawerKt : BaseDrawerKt {
    private val TAG = "PathDrawer"
    private var data = CopyOnWriteArrayList<Int>()
    private lateinit var dataPath: Path

    // 两个数据 X轴方向的距离
    private var dataSpaceX: Float = 0f

    // 每小格的边长
    private var smallGridSpace: Float = 0f

    // 每大格 X方向画多少个数据点，可设
    private var dataNumbersPerGrid = 54

    // Y是 0的位置，从上往下偏移多少大格
    private var offset = 5

    // 每一大格代表多少毫伏，可设
    private var mvPerLargeGrid = 1000f

    // 水平平移记录点
    private var lastX: Float = 0f

    // 惯性滑动
    private lateinit var mScroller: Scroller

    // 速度追踪
    private var mVelocityTracker = VelocityTracker.obtain()

    // 记录两指按下的点
    private var pointOne: PointF = PointF()
    private var pointTwo: PointF = PointF()

    // 用于每次双指按下时，记录当次拉伸事件 Y增益初始值
    private var mvPerLargeGridOnThisTime = 1000f

    // 用于每次双指按下时，记录当次拉伸事件 X增益初始值
    private var numbersPerLargeGridOnThisTime = 54

    /**
     * 重置
     */
    public fun reset() {
        this.lastX = 0f
        this.dataNumbersPerGrid = 54
        this.mvPerLargeGrid = 1000f
        this.smallGridSpace = this.mBaseChart.getGridSpace()
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid
        if (dataPath != null) dataPath.reset()
        data.clear()
        update()
    }

    constructor(baseChart: ECGRealTimeChartKt) {
        this.mBaseChart = baseChart
        this.linePaint = Paint()
        this.linePaint.color = Color.RED
        this.linePaint.strokeWidth = 5f
        this.linePaint.style = Paint.Style.STROKE
        this.linePaint.isDither = true
        this.linePaint.isAntiAlias = true
        this.dataPath = Path()
        this.smallGridSpace = this.mBaseChart.getGridSpace()
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid
        mScroller = Scroller(mBaseChart.context)
    }

    public fun setPaintWidth(paintWidth: Float) {
        this.linePaint.strokeWidth = paintWidth
        update()
    }

    /**
     * 重新设置采样率
     *
     * @param numbers
     */
    public fun setDataNumbersPerGrid(numbers: Int) {
        var team: Int = numbers
        if (numbers < 27) team = 27
        if (numbers > 66) team = 66

        this.dataNumbersPerGrid = team
        this.dataSpaceX = this.smallGridSpace * 5 / this.dataNumbersPerGrid
        update()
    }

    /**
     * 获取每小格显示的数据个数，再结合医疗版的采样率，就可以算出一格显示了多长时间的数据
     *
     * @return
     */
    public fun getDataNumbersPerGrid(): Int {
        return this.dataNumbersPerGrid;
    }

    /**
     * 设置每大格代表多少毫伏
     *
     * @param mv 毫伏
     */
    public fun setMvPerLargeGrid(mv: Float) {
        var team = mv
        if (mv < 500) team = 500f
        if (mv > 1500) team = 1500f

        this.mvPerLargeGrid = team
        update();
    }

    /**
     * @return 获取没大哥代表多少毫伏
     */
    public fun getMvPerLargeGrid(): Float {
        return this.mvPerLargeGrid;
    }

    public fun getData(): List<Int> {
        return this.data
    }

    /**
     * @param dats 添加数据
     */
    public fun addData(dats: IntArray) {
        for (item in dats) {
            this.data.add(item)
        }
        update()
    }

    /**
     * @param dats 添加数据
     */
    public fun addData(dats: List<Int>) {
        this.data.addAll(dats);
        update()
    }

    /**
     * @param dat 添加数据
     */
    public fun addData(dat: Int) {
        this.data.add(dat)
        update()
    }

    /**
     * 清除数据
     */
    public fun clearData() {
        this.data.clear()
        update()
    }

    /**
     * 更新 UI
     */
    private fun update() {
        if (createPath()) {
            mBaseChart.postInvalidate()
        }
    }

    /**
     * 创建曲线
     */
    private fun createPath(): Boolean {
        // 曲线长度超过控件宽度，曲线起点往左移
        var startX = if (this.data.size * dataSpaceX > viewWidth) // Kotlin三元运算符
            (viewWidth - (this.data.size * dataSpaceX)) else 0f
        dataPath.reset()
        for (i in 0 until this.data.size) {
            var x = startX + i * this.dataSpaceX
            var y = getVisibleY(this.data.get(i))
            if (i == 0) {
                dataPath.moveTo(x, y)
            } else {
                dataPath.lineTo(x, y)
            }
        }
        return true;
    }

    private lateinit var copyPath: Path
    private fun copyPath(): Path {
        copyPath = Path()
        // 曲线长度超过控件宽度，曲线起点往左移
        var startX = if (this.data.size * dataSpaceX > viewWidth)
            (viewWidth - (this.data.size * dataSpaceX)) else 0f
        copyPath.reset();
        for (i in 0 until this.data.size) {
            var x = startX + i * this.dataSpaceX
            var y = getVisibleY(this.data.get(i)) + smallGridSpace * 5 * 5
            if (i == 0) {
                copyPath.moveTo(x, y);
            } else {
                copyPath.lineTo(x, y);
            }
        }
        return copyPath;
    }

    /**
     * 电压 mv（毫伏）在 Y轴方向的换算
     * 屏幕向上往下是 Y 轴正方向，所以电压值要乘以 -1进行翻转
     * 目前默认每一大格代表 1000 mv，而真正一大格的宽度只有 150,所以 data要以两数换算
     * Y == 0，是在 View的上边缘，所以要向下偏移将波形显示在中间
     *
     * @param data
     * @return
     */
    private fun getVisibleY(data: Int): Float {
        // 电压值换算成 Y值
        var visibleY = -smallGridSpace * 5 / mvPerLargeGrid * data;
        // 向下偏移
        return visibleY + smallGridSpace * 5 * offset;
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(dataPath, linePaint);
        canvas.drawPath(copyPath(), linePaint);
    }

    override fun onSizeChange() {
        super.onSizeChange()
        createPath()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "pointerCount = " + event.getPointerCount());
        if (event.pointerCount == 1) {
            singlePoint(event)
        }
        if (event.pointerCount == 2) {
            doublePoint(event)
        }
        return true
    }

    /**
     * @param event 单指事件
     */
    private fun singlePoint(event: MotionEvent) {
        mVelocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.getX()
            }

            MotionEvent.ACTION_MOVE -> {
                var deltaX = event.x - lastX
                delWithActionMove(deltaX)
                lastX = event.x
            }

            MotionEvent.ACTION_UP -> {
                // 计算滑动速度
                computeVelocity();
            }
        }
    }

    /**
     * @param event 双指事件
     */
    private fun doublePoint(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN ->{ // 第二根手指按下
                Log.d(TAG, "ACTION_POINTER_DOWN")
                saveLastPoint(event)
                numbersPerLargeGridOnThisTime = getDataNumbersPerGrid()
                mvPerLargeGridOnThisTime = getMvPerLargeGrid()
            }

            MotionEvent.ACTION_MOVE -> { // 双指拉伸
                Log.d(TAG, "ACTION_MOVE")
                getScaleX(event)
                getScaleY(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {   // 先离开的手指
                Log.d(TAG, "ACTION_POINTER_UP")
            }
        }
    }
    /**
     * 处理 Y方向的拉伸
     *
     * @param event 事件
     * @return 拉伸量
     */
    private fun getScaleY(event: MotionEvent): Float {
        var pointOneY = event.getY(0)
        var pointTwoY = event.getY(1)
        // 计算 Y轴方向的拉伸量
        var deltaScaleY = Math.abs(pointOneY - pointTwoY) - Math.abs(pointOne.y - pointTwo.y)
        // 算出最终增益
        var perMV = mvPerLargeGridOnThisTime - deltaScaleY
        setMvPerLargeGrid(perMV)
        return deltaScaleY
    }

    /**
     * 处理 X方向的拉伸
     *
     * @param event 事件
     * @return 拉伸量
     */
    private fun getScaleX(event: MotionEvent): Float {
        var pointOneX = event.getX(0)
        var pointTwoX = event.getX(1)
        // 算出 X轴方向的拉伸量
        var deltaScaleX = Math.abs(pointOneX - pointTwoX) - Math.abs(pointOne.x - pointTwo.x)
        // 设置拉伸敏感度
        var inDevi = mBaseChart.getWidth() / 54
        // 计算拉伸时增益偏移量
        var inDe = (deltaScaleX / inDevi).toInt()
        // 算出最终增益
        var perNumber = numbersPerLargeGridOnThisTime - inDe
        // 设置增益
        setDataNumbersPerGrid(perNumber)
        return deltaScaleX
    }

    /**
     * 记录双指按下的点
     *
     * @param event 事件
     */
    private fun saveLastPoint(event: MotionEvent) {
        pointOne.x = event.getX(0)
        pointOne.y = event.getY(0)
        pointTwo.x = event.getX(1)
        pointTwo.y = event.getY(1)
    }

    /**
     * @param deltaX 处理 MOVE事件
     */
    private fun delWithActionMove(deltaX: Float) {
        if (this.data.size * dataSpaceX <= viewWidth) return
        var leftBorder = getLeftBorder() // 左边界
        var rightBorder = getRightBorder() // 右边界
        var scrollX = mBaseChart.scrollX // X轴滑动偏移量

        if ((scrollX <= leftBorder) && (deltaX > 0)) {
            mBaseChart.scrollTo(((viewWidth - this.data.size * dataSpaceX).toInt()), 0)
        } else if ((scrollX >= rightBorder) && (deltaX < 0)) {
            mBaseChart.scrollTo(0, 0)
        } else {
            mBaseChart.scrollBy((-deltaX).toInt(), 0)
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mBaseChart.scrollTo(mScroller.currX, 0)
        }
    }

    /**
     * 计算滑动速度
     */
    private fun computeVelocity() {
        mVelocityTracker.computeCurrentVelocity(500)
        var velocityX = mVelocityTracker.xVelocity
        // 初始化 Scroller
        Log.d(TAG, "velocityX = $velocityX")
        fling(mBaseChart.getScrollX(), 0, (- velocityX).toInt(), 0,
            getLeftBorder(), getRightBorder(), 0, 0)
    }

    /**
     * @param startX    起始 X
     * @param startY    起始 Y
     * @param velocityX X 方向速度
     * @param velocityY Y 方向速度
     * @param minX      左边界
     * @param maxX      右边界
     * @param minY      上边界
     * @param maxY      下边界
     */
    private fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int,
     minX: Int, maxX: Int, minY: Int, maxY: Int) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
    }

    /**
     * @return 左边界
     */
    private fun getLeftBorder(): Int {
        var left = 0
        if (this.data.size * dataSpaceX > viewHeight) {
            left = ((viewWidth - this.data.size * dataSpaceX).toInt());
        }
        return left
    }

    /**
     * @return 右边界
     */
    private fun getRightBorder(): Int {
        return 0;
    }

}