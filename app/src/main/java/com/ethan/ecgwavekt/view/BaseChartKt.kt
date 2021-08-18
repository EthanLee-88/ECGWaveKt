package com.ethan.ecgwavekt.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

 abstract class BaseChartKt: View {  // 继承关系用 :

    constructor(context : Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet,0)

    constructor(context: Context, attributeSet: AttributeSet?, style: Int) : super(context, attributeSet, style){
        init()
    }

    private fun init(){

    }

}