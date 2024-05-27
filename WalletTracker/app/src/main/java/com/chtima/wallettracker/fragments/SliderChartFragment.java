package com.chtima.wallettracker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chtima.wallettracker.R;
import com.chtima.wallettracker.swipeTouch.OnSwipeTouchListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SliderChartFragment extends Fragment {

    private List<Chart<?>> list = new ArrayList<>();

    private int currentIndex = 1;

    private OnSwipeTouchListener onSwipeTouchListener;

    public static SliderChartFragment newInstance() {
        return new SliderChartFragment();
    }

    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_chart, container, false);

        title = view.findViewById(androidx.core.R.id.title);

        onSwipeTouchListener = new OnSwipeTouchListener(requireContext(), new OnSwipeTouchListener.onSwipe() {
            @Override
            public void onSwipeLeft() {
                Log.e("SWIPE", "LEFT");
                currentIndex = (currentIndex + 1) % list.size();
                updateChartPositions();
            }

            @Override
            public void onSwipeRight() {
                Log.e("SWIPE", "RIGHT");
                currentIndex = (currentIndex - 1 + list.size()) % list.size();
                updateChartPositions();
            }
        });

        list.add(view.findViewById(R.id.bar_chart_last_week));
        BarChart barChartLastWeek = (BarChart) list.get(0);
        barChartLastWeek.getLegend().setEnabled(false);
        barChartLastWeek.getDescription().setEnabled(false);


        list.add(view.findViewById(R.id.pie_chart_today));
        PieChart pieChart = (PieChart)list.get(1);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(getResources().getColor(R.color.transparent, null));
        pieChart.setRotationEnabled(false);
        pieChart.setTouchEnabled(true);

        list.add(view.findViewById(R.id.bar_chart_this_week));
        BarChart barChartThisWeek = (BarChart) list.get(2);
        barChartThisWeek.getLegend().setEnabled(false);
        barChartThisWeek.getDescription().setEnabled(false);

        list.get(0).setTranslationX(-1000);
        list.get(2).setTranslationX(+1000);

        list.forEach(x -> x.setOnTouchListener((v, event) ->  {
            boolean result = x.onTouchEvent(event);
            onSwipeTouchListener.onTouch(v, event);
            return result;
        }));

        return view;
    }

    public void setPieChartToday(List<PieEntry> pieChartToday){

        PieDataSet ds1 = new PieDataSet(pieChartToday, "transaction");

        ds1.setSliceSpace(3f);
        ds1.setSelectionShift(5f);
        ds1.setColors(getResources().getColor(R.color.saffron, null),
                getResources().getColor(R.color.peach, null),
                getResources().getColor(R.color.silver_sand, null),
                getResources().getColor(R.color.lilac_grey, null));

        PieData pieData = new PieData(ds1);
        pieData.setValueTypeface(getResources().getFont(R.font.outfit_medium));
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(getResources().getColor(R.color.white, null));

        PieChart pieChart = (PieChart) list.get(1);

        pieChart.setEntryLabelTypeface(getResources().getFont(R.font.nunito_regular));

        pieChart.setData(pieData);
        pieChart.invalidate();

    }
    public void setBarChartLastWeek(List<BarEntry> barChartLastWeek){

        BarChart barChart = (BarChart) list.get(0);

        BarDataSet dataSet = new BarDataSet(barChartLastWeek, "transaction");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));

        barChart.invalidate();

    }

    public void setBarChartThisWeek(List<BarEntry> barChartThisWeek){

        BarChart barChart = (BarChart) list.get(2);

        BarDataSet dataSet = new BarDataSet(barChartThisWeek, "transaction");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));

        barChart.invalidate();
    }

    private void updateChartPositions() {

        title.setText(currentIndex == 0 ? R.string.last_week : currentIndex == 2 ? R.string.this_week : R.string.today);

        for (int i = 0; i < list.size(); i++) {
            if (i == currentIndex) {
                list.get(i).setTranslationX(0); // center
            } else if (i == (currentIndex + 1) % list.size()) {
                list.get(i).setTranslationX(1000); // right
            } else if (i == (currentIndex - 1 + list.size()) % list.size()) {
                list.get(i).setTranslationX(-1000); // left
            } else {
                list.get(i).setTranslationX(1000); // hide
            }
        }
    }

}