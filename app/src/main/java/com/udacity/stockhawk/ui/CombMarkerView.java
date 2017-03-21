package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.udacity.stockhawk.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

public class CombMarkerView extends MarkerView {
    private TextView marker_date;
    private TextView marker_AD;
    private List<HistoricalQuote> mDataList;
    public CombMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        marker_date = (TextView) findViewById(R.id.marker_date);
        marker_AD = (TextView) findViewById(R.id.marker_AD);
    }
    public void setData(List<HistoricalQuote> data){
        this.mDataList =data;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        HistoricalQuote historicalQuote= mDataList.get(highlight.getXIndex());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formatDate = sdf.format(historicalQuote.getDate().getTime());
        float t=(historicalQuote.getClose().floatValue()-historicalQuote.getOpen().floatValue())/historicalQuote.getOpen().floatValue();
        marker_date.setText(formatDate);
        if (t<0){
            marker_AD.setTextColor(Color.parseColor("#D50000"));
        }else if (t>0){
            marker_AD.setTextColor(Color.parseColor("#00C853"));
        }else{
            marker_AD.setTextColor(Color.parseColor("#FFFFFF"));
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(t*100);
        marker_AD.setText(p+"%");
        String contentDes=getContext().getString(R.string.content_des_combmarkerview_start)
                +historicalQuote.getSymbol()+formatDate
                +getContext().getString(R.string.content_des_combmarkerview_end)
                +getContext().getString(R.string.content_des_stock_gains)+p+"%"
                +getContext().getString(R.string.volume)+historicalQuote.getVolume();
        this.setContentDescription(contentDes);

   }

    @Override
    public int getXOffset(float xpos) {
        return -getWidth()/2;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }

}