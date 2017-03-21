package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.udacity.stockhawk.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

public class BarMarkerView extends MarkerView {
    private TextView marker_date;
    private TextView marker_AD;
    private List<HistoricalQuote> mDataList;
    public BarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        marker_date = (TextView) findViewById(R.id.marker_date);
        marker_AD = (TextView) findViewById(R.id.marker_AD);
    }
    public void setData(List<HistoricalQuote> data){
        this.mDataList =data;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        marker_date.setVisibility(View.GONE);
        HistoricalQuote historicalQuote= mDataList.get(highlight.getXIndex());
        marker_AD.setText(getContext().getString(R.string.volume)+historicalQuote.getVolume());
        marker_AD.setTextColor(Color.parseColor("#E7B448"));
   }

    @Override
    public int getXOffset(float xpos) {
        return -getWidth()*3/4;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }

}