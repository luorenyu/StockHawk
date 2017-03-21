package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = StockWidgetRemoteViewsService.class.getSimpleName();
    // these indices must match the projection
    static final int INDEX_WEATHER_ID = 0;
    static final int INDEX_WEATHER_DATE = 1;
    static final int INDEX_WEATHER_CONDITION_ID = 2;
    static final int INDEX_WEATHER_DESC = 3;
    static final int INDEX_WEATHER_MAX_TEMP = 4;
    static final int INDEX_WEATHER_MIN_TEMP = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
//                data = getContentResolver().query(
//                        Contract.Quote.URI,
//                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
//                        null, null, Contract.Quote.COLUMN_SYMBOL);
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");

                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item_quote);
                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                String price = dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);

                if (rawAbsoluteChange > 0) {
                    views.setTextColor(R.id.change, Color.parseColor(getString(R.string.green)));
                } else {
                    views.setTextColor(R.id.change, Color.parseColor(getString(R.string.red)));
                }
                views.setTextViewTextSize(R.id.symbol, TypedValue.COMPLEX_UNIT_SP,18);
                views.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_SP,18);
                views.setTextViewTextSize(R.id.change, TypedValue.COMPLEX_UNIT_SP,18);
                views.setTextViewText(R.id.symbol,symbol);
                views.setTextViewText(R.id.price,price);
                views.setTextViewText(R.id.change,change);

//                String description = data.getString(INDEX_WEATHER_DESC);
                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(StockWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(weatherUri);
                fillInIntent.putExtra("symbol",symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_WEATHER_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
