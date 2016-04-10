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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.yoavgibri.cash.DataBase.DBHelper;

import java.util.ArrayList;


public class YearViewFragment extends Fragment implements OnChartValueSelectedListener {

    private OnFragment_YearView_InteractionListener mListener;
    private BarChart mBarChart;
    private DBHelper mHelper;
    private int mYearTotal;
    private ArrayList<String> mXVals;

    public YearViewFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_year_view, container, false);

        mHelper = new DBHelper(getActivity());
        TextView mTotalExpenseYear = (TextView) v.findViewById(R.id.textViewAnalyticsYearTotalMonth);
        String year = "2016";

        mBarChart = (BarChart) v.findViewById(R.id.barChart);

        startChart();
        mTotalExpenseYear.setText(year + "-   " + mYearTotal);

        return v;
    }
//        String[] months = new String[]{"jan", "feb", "mer", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    private void startChart() {
        ArrayList<BarEntry> yVals = new ArrayList<>();
        mXVals = new ArrayList<>();

        Cursor c = mHelper.getTotalsByMonth();
        int i = 0;
        while (c.moveToNext()) {
            int amount = c.getInt(c.getColumnIndex("SUM(" + DBHelper.COL_AMOUNT + ")"));
            BarEntry entry = new BarEntry(amount, i, "label" + i);
            yVals.add(entry);

            String month = c.getString(c.getColumnIndex(DBHelper.COL_MONTH));
            mXVals.add(month);

            i++;

            mYearTotal += amount;
        }
        c.close();

        BarDataSet set = new BarDataSet(yVals, "MyLabel");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.BLACK);


        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);


        BarData data = new BarData(mXVals, dataSets);
        mBarChart.setData(data);
        mBarChart.invalidate();
        mBarChart.setTouchEnabled(true);
        mBarChart.setOnChartValueSelectedListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            Toast.makeText(getActivity(), "on button selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragment_YearView_InteractionListener) {
            mListener = (OnFragment_YearView_InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragment_YearView_InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (mListener != null) {
            mListener.onFragment_YearView_Interaction(mXVals.get(e.getXIndex()), (int) e.getVal());
        }
    }

    @Override
    public void onNothingSelected() {
        Toast.makeText(getActivity(), "nothing selected", Toast.LENGTH_SHORT).show();
    }


    public interface OnFragment_YearView_InteractionListener {
        void onFragment_YearView_Interaction(String month, int monthTotal);
    }
}
