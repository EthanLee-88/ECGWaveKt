package com.ethan.ecgwavekt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ethan.ecgwave.view.kti.ECGRealTimeChartKt

class MainActivity : AppCompatActivity() {

    private lateinit var mECGRealTimeChart: ECGRealTimeChartKt
    private var team =  ArrayList<Int>();
    private var data = arrayOf( 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        51, 60, 71, 88, 90, 92, 96, 122,
        125, 122, 96, 122, 129, 168, 128, 109, 100, 89,
        128, 178, 199, 256, 109, 188, 256, 1988, 2012, 2041,1999,
        2399, 256, 128, 109, 88, 67, 23, 167, 256, 562,
        235, 109, 56, 33, 12, 150, 123, 109, 99, 88,
        77, 67, 55, 34, 12, 45, 99, 156, 199, 256,
        235, 209, 200, 188, 169, 150, 123, 109, 99, 88,0,0,0,0,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mECGRealTimeChart = findViewById(R.id.ecg_chart)
    }
    public fun onClick(view: View){
        if (mECGRealTimeChart.getData().isEmpty()){
             Thread(Runnable {
                addDataDelay(data);
            }).start();
        }else {
            mECGRealTimeChart.clearData();
        }
    }

     private fun addDataDelay(data: Array<Int>) {
        mECGRealTimeChart.setNoDataComing(false);
        for (i in 0 until 10) {
                for (item in data) {
                    team.add(item);
                    if (team.size >= 6){
                        mECGRealTimeChart.addData(team);
                        Thread.sleep(68);
                        team.clear();
                    }
                }
        }
        mECGRealTimeChart.setNoDataComing(true);
    }
}