package com.yoavgibri.cash;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yoavgibri.cash.DataBase.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class MonthViewFragment extends Fragment implements OnChartValueSelectedListener {

    private OnFragment_MonthView_InteractionListener mListener;
    private PieChart mPieChart;
    private DBHelper mHelper;

    public MonthViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month_view, container, false);
        mHelper = new DBHelper(getActivity());

        mPieChart = (PieChart) view.findViewById(R.id.PieChart);

        Bundle bundle = this.getArguments();
        String month = bundle.getString("month");
        int monthTotal = bundle.getInt("monthTotal",0);
        ArrayList<Expense> expenses = mHelper.getExpensesByMonth(month);



        startChart(month);

        return view;
    }

    private void startChart(String month) {
        ArrayList<Entry> totals = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();

        Cursor c = mHelper.getMonthTotalsByType(month);
        int i = 0;
        while (c.moveToNext()){
            Entry entry = new Entry(c.getInt(c.getColumnIndex("SUM(" + DBHelper.COL_AMOUNT + ")")), i);
            totals.add(entry);

            String type = c.getString(c.getColumnIndex(DBHelper.COL_TYPE));
            types.add(type);
        }
        c.close();



        PieDataSet set = new PieDataSet(totals, "My Pie Label");
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColor(Color.BLACK);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int color : ColorTemplate.PASTEL_COLORS)
            colors.add(color);

        set.setColors(colors);


        PieData data = new PieData(types, set);
        mPieChart.setData(data);
        mPieChart.invalidate();
        mPieChart.setTouchEnabled(true);
        mPieChart.setOnChartValueSelectedListener(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragment_MonthView_Interaction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragment_MonthView_InteractionListener) {
            mListener = (OnFragment_MonthView_InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragment_MonthView_InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


    public interface OnFragment_MonthView_InteractionListener {
        void onFragment_MonthView_Interaction(Uri uri);
    }
}
