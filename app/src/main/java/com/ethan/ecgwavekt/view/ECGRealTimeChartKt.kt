package com.ethan.ecgwave.view.kti

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import com.ethan.scanview.view.kti.NumericalUnitDrawerKt
import com.ethan.scanview.view.kti.PathDrawerKt

class ECGRealTimeChartKt : BaseChartKt {
    private val TAG = "ECGRealTimeChart"
    // 画网格
    private lateinit var mBackgroundDrawer: GridDrawerKt
    // 画曲线
    private lateinit var mPathDrawerKt: PathDrawerKt
    // 画单位
    private lateinit var mNumericalUnitDrawerKt: NumericalUnitDrawerKt
    // 没数据来时才去设置参数以及滑动
    private var noDataComing = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) :
            super(context, attributeSet, defStyle) {
            init(context, attributeSet, defStyle)
    }

    private fun init(context: Context, attributeSet: AttributeSet?, defStyle: Int) {
        this.mBackgroundDrawer = GridDrawerKt(this)
        this.mPathDrawerKt =  PathDrawerKt(this)
        this.mNumericalUnitDrawerKt =  NumericalUnitDrawerKt(this)
    }

    /**
     * @param noData 没数据来了
     */
    public fun setNoDataComing(noData: Boolean) {
        this.noDataComing = noData
    }

    public fun getNoDataComing(): Boolean {
        return this.noDataComing;
    }

    /**
     * 设置横坐标每大格内显示的数据点数，即 X轴方向的增益
     *
     * @param numbersPerGrid 数据点
     */
    public fun setDataNumbersPerGrid(numbersPerGrid: Int){
        this.mPathDrawerKt.setDataNumbersPerGrid(numbersPerGrid)
    }

    /**
     * @return 横坐标每大格点数
     */
    public fun getDataNumbersPerGrid(): Int{
        return this.mPathDrawerKt.getDataNumbersPerGrid()
    }

    /**
     * 设置纵坐标每大格代表多少毫伏，即纵坐标增益
     *
     * @param mv 毫伏
     */
    public fun setMvPerLargeGrid(mv: Float){
        this.mPathDrawerKt.setMvPerLargeGrid(mv)
    }

    /**
     * @return 纵坐标每大格毫伏数
     */
    public fun getMvPerLargeGrid(): Float{
        return this.mPathDrawerKt.getMvPerLargeGrid()
    }


    /**
     * @return 每小格边长
     */
    public fun getGridSpace(): Float {
        return mBackgroundDrawer.getGridSpace()
    }

    /**
     * 清除数据
     */
    public fun clearData() {
        this.mPathDrawerKt.clearData()
        this.mPathDrawerKt.reset()
        scrollTo(0, 0)
    }

    /**
     * @param data 添加数据
     */
    public fun addData(data: IntArray) {
        this.mPathDrawerKt.addData(data)
    }

    /**
     * @param data 添加数据
     */
    public fun addData(data: Int) {
        this.mPathDrawerKt.addData(data)
    }

    /**
     * @param data 添加数据
     */
    public fun addData(data: List<Int>) {
        this.mPathDrawerKt.addData(data)
    }

    /**
     * 获取当前数据
     */
    public fun getData(): List<Int> {
        return this.mPathDrawerKt.getData()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBackgroundDrawer.onSizeChange()
        mPathDrawerKt.onSizeChange()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        setBackgroundColor(Color.parseColor("#dd000000"))
    }

    override fun onDraw(canvas: Canvas) {
        this.mBackgroundDrawer.onDraw(canvas)
        this.mPathDrawerKt.onDraw(canvas)
        this.mNumericalUnitDrawerKt.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!getNoDataComing()) return false
        return mPathDrawerKt.onTouchEvent(event)
    }

    override fun computeScroll() {
        this.mPathDrawerKt.computeScroll()
    }

}