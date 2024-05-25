package com.chtima.wallettracker.fragments;

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
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class SliderChartFragment extends Fragment {

    private List<Chart<?>> list = new ArrayList<>();

    private int currentIndex = 1;

    public static SliderChartFragment newInstance() {
        return new SliderChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_chart, container, false);

        list.add(view.findViewById(R.id.bar_chart_last_week));



        list.add(view.findViewById(R.id.pie_chart_today));
        PieChart pieChart = (PieChart)list.get(1);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(getResources().getColor(R.color.transparent, null));
        pieChart.setRotationEnabled(false);
        pieChart.setTouchEnabled(false);

        list.add(view.findViewById(R.id.bar_chart_this_week));



        list.get(0).setTranslationX(-1000);
        list.get(2).setTranslationX(+1000);


        view.setOnTouchListener(new OnSwipeTouchListener(requireContext(), new OnSwipeTouchListener.onSwipe() {
            @Override
            public void onSwipeLeft() {
                Log.e("SWIPE", "LEFT");
                currentIndex = (currentIndex + 1) % list.size();
                updateTextViewPositions();

            }

            @Override
            public void onSwipeRight() {
                Log.e("SWIPE", "RIGHT");
                currentIndex = (currentIndex - 1 + list.size()) % list.size();
                updateTextViewPositions();
            }
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

    private void updateTextViewPositions() {
        for (int i = 0; i < list.size(); i++) {
            if (i == currentIndex) {
                list.get(i).setTranslationX(0); // Центр экрана
            } else if (i == (currentIndex + 1) % list.size()) {
                list.get(i).setTranslationX(1000); // Справа за экраном
            } else if (i == (currentIndex - 1 + list.size()) % list.size()) {
                list.get(i).setTranslationX(-1000); // Слева за экраном
            } else {
                list.get(i).setTranslationX(1000); // Прячем остальные (можно изменить по необходимости)
            }
        }
    }

}