package com.chtima.wallettracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chtima.wallettracker.R
import com.chtima.wallettracker.swipeTouch.OnSwipeTouchListener
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class SliderChartFragment private constructor(): Fragment() {

    private val list: MutableList<Chart<*>> = ArrayList()
    private var currentIndex = 1
    private lateinit var onSwipeTouchListener: OnSwipeTouchListener
    private lateinit var title: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v : View = inflater.inflate(R.layout.fragment_slider_chart, container, false)

        title = v.findViewById(androidx.core.R.id.title)

        onSwipeTouchListener = OnSwipeTouchListener(requireContext(), object: OnSwipeTouchListener.onSwipe{
            override fun onSwipeLeft() {
                currentIndex = (currentIndex + 1) % list.size;
                updateChartPositions()
            }

            override fun onSwipeRight() {
                currentIndex = (currentIndex - 1 + list.size % list.size)
                updateChartPositions()
            }
        })

        list.add(v.findViewById(R.id.bar_chart_last_week))
        val barChartLastWeek = list[0] as BarChart
        barChartLastWeek.legend.isEnabled = false
        barChartLastWeek.description.isEnabled = false

        list.add(v.findViewById(R.id.pie_chart_today))
        val pieChart = list[1] as PieChart
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setHoleColor(resources.getColor(R.color.transparent, null))
        pieChart.isRotationEnabled = false
        pieChart.setTouchEnabled(true)

        list.add(v.findViewById(R.id.bar_chart_this_week))
        val barChartThisWeek = list[2] as BarChart
        barChartThisWeek.legend.isEnabled = false
        barChartThisWeek.description.isEnabled = false

        list[0].translationX = -1000f
        list[2].translationX = 1000f

        list.forEach { chart ->
            chart.setOnTouchListener { v, event ->
                chart.onTouchEvent(event)
                onSwipeTouchListener.onTouch(v, event)
                true
            }
        }
        return v
    }

    fun setPieChartToday(pieChartToday:List<PieEntry>){
        if (list.isEmpty()) return

        val pieChart = list[1] as PieChart
        val ds1 = PieDataSet(pieChartToday, "transaction")

        if (pieChartToday.isEmpty() || pieChartToday.sumOf { it.value.toDouble() } == 0.0) {
            setNullToChart(pieChart)
            return
        }

        ds1.sliceSpace = 3f
        ds1.selectionShift = 3f
        ds1.colors = listOf(
            resources.getColor(R.color.saffron, null),
            resources.getColor(R.color.peach, null),
            resources.getColor(R.color.silver_sand, null),
            resources.getColor(R.color.lilac_grey, null)
        )

        val pieData = PieData(ds1)
        pieData.setValueTypeface(resources.getFont(R.font.outfit_medium))
        pieData.setValueTextSize(16f)
        pieData.setValueTextColor(resources.getColor(R.color.white, null))

        pieChart.setEntryLabelTypeface(resources.getFont(R.font.outfit_regular))
        pieChart.data = pieData
        pieChart.invalidate()

    }

    fun setBarChartLastWeek(barChartLastWeek: List<BarEntry>) {

        val barChart = list[0] as BarChart

        if (barChartLastWeek.isEmpty() || barChartLastWeek.sumOf { it.y.toDouble() } == 0.0) {
            setNullToChart(barChart)
            return
        }

        val dataSet = BarDataSet(barChartLastWeek, "transaction")
        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))

        barChart.invalidate()
    }

    fun setBarChartThisWeek(barChartThisWeek: List<BarEntry>) {
        if (list.isEmpty()) return

        val barChart = list[2] as BarChart

        if (barChartThisWeek.isEmpty() || barChartThisWeek.sumOf { it.y.toDouble() } == 0.0) {
            setNullToChart(barChart)
            return
        }

        val dataSet = BarDataSet(barChartThisWeek, "transaction")
        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))

        barChart.invalidate()
    }

    private fun setNullToChart(chart: Chart<*>){
        chart.setData(null);
        chart.setNoDataText(getString(R.string.no_completed_transactions_for_today));
        chart.invalidate();
    }

    private fun updateChartPositions() {
        title.setText(
            when (currentIndex) {
                0 -> R.string.last_week
                2 -> R.string.this_week
                else -> R.string.today
            }
        )

        for (i in list.indices) {
            when (i) {
                currentIndex -> list[i].translationX = 0f
                (currentIndex + 1) % list.size -> list[i].translationX = 1000f
                (currentIndex - 1 + list.size) % list.size -> list[i].translationX = -1000f
                else -> list[i].translationX = 1000f
            }
        }
    }

    companion object{
        fun newInstance():SliderChartFragment{
            return SliderChartFragment()
        }

    }
}