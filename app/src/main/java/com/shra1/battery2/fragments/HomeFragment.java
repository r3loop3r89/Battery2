package com.shra1.battery2.fragments;


import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.shra1.battery2.R;
import com.shra1.battery2.database.MDB;
import com.shra1.battery2.mymodels.BatteryEntry;
import com.shra1.battery2.utils.Constants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    static HomeFragment instance = null;
    Context mCtx;
    MDB mdb;
    DateTime dateTime;
    private ImageButton ibPrev;
    private TextView tvDate;
    private ImageButton ibNext;
    private LineChart chart;

    public HomeFragment() {
    }

    public static HomeFragment getInstance() {
        if (instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCtx = container.getContext();
        mdb = MDB.getInstance(mCtx);
        View v = LayoutInflater.from(mCtx).inflate(R.layout.home_fragment, container, false);

        initViews(v);

        dateTime = new DateTime();
        fetchDataForDate();

        ibPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime = dateTime.minusDays(1);
                fetchDataForDate();
            }
        });

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime = dateTime.plusDays(1);
                fetchDataForDate();
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime = new DateTime();
                fetchDataForDate();
            }
        });

        return v;
    }

    private void initViews(View v) {
        ibPrev = (ImageButton) v.findViewById(R.id.ibPrev);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        ibNext = (ImageButton) v.findViewById(R.id.ibNext);
        chart = (LineChart) v.findViewById(R.id.chart);
    }

    private void fetchDataForDate() {
        tvDate.setText(dateTime.toString(Constants.DATE_FORMAT));
        //pbDFProgressBar.setVisibility(View.VISIBLE);
        BatteryEntry.DBCommands.getBatteryEntriesFor(MDB.getInstance(mCtx).getReadableDatabase(),
                dateTime,
                new BatteryEntry.GetAllBatteryEntriesCallback() {
                    @Override
                    public void onCompleted(List<BatteryEntry> l) {
                        //pbDFProgressBar.setVisibility(View.GONE);
                        if (l.size() == 0) {
                            String text = tvDate.getText().toString();
                            text = text + " NO DATA";
                            tvDate.setText(text);
                            return;
                        }

                        List<Entry> entries = new ArrayList<Entry>();
                        for (BatteryEntry e : l) {
                            entries.add(new Entry(e.getBatteryEntryOn(), e.getBatteryLevel()));
                        }
                        LineDataSet dataSet = new LineDataSet(entries, "Shra1");
                        dataSet.setDrawCircles(false);
                        dataSet.setDrawValues(false);
                        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                        dataSet.setLineWidth(5f);
                        dataSet.setColor(getContext().getResources().getColor(R.color.colorAccent));

                        LineData lineData = new LineData(dataSet);

                        chart.setTouchEnabled(true);

                        IMarker myMarkerView = new MyMarkerView(mCtx, R.layout.chart_marker_view_layout);

                        chart.setData(lineData);
                        chart.setMarker(myMarkerView);
                        chart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaximum(100f);
                        chart.getAxis(YAxis.AxisDependency.LEFT).setAxisMinimum(0f);
                        chart.getXAxis().setValueFormatter(new MyValueFormatter());

                        chart.getAxisLeft().setDrawGridLines(false);
                        chart.getAxisRight().setDrawGridLines(false);
                        chart.getAxisRight().setDrawLabels(false);
                        chart.getXAxis().setDrawGridLines(false);
                        chart.getXAxis().setDrawLabels(false);


                        Description d = new Description();
                        d.setText("Battery data");
                        chart.setDescription(d);

                        chart.invalidate();
                    }
                });
    }

    class MyMarkerView extends MarkerView {
        TextView tvMarkerView, tvTime;
        private int uiScreenWidth;

        /**
         * Constructor. Sets up the MarkerView with a custom layout resource.
         *
         * @param context
         * @param layoutResource the layout resource to use for the MarkerView
         */
        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvMarkerView = findViewById(R.id.tvMarkerView);
            tvTime = findViewById(R.id.tvTime);
            uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            super.refreshContent(e, highlight);
            DateTime dateTime = new DateTime((long) e.getX());
            tvMarkerView.setText("" + (int) e.getY() + " %");
            tvTime.setText(dateTime.toString(Constants.TIME_FORMAT));
        }

        @Override
        public void draw(Canvas canvas, float posx, float posy){
            // Check marker position and update offsets.
            int w = getWidth();
            if((uiScreenWidth-posx-w) < w) {
                posx -= w;
            }

            // translate to the correct position and draw
            canvas.translate(posx, posy);
            draw(canvas);
            canvas.translate(-posx, -posy);
        }
    }

    class MyValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            DateTime dateTime = new DateTime((long) value);
            return dateTime.toString(Constants.TIME_FORMAT);
        }
    }
}
