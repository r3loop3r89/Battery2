package com.shra1.battery2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shra1.battery2.R;
import com.shra1.battery2.adapters.BatteryEntryDetailsListAdapter;
import com.shra1.battery2.database.MDB;
import com.shra1.battery2.mymodels.BatteryEntry;
import com.shra1.battery2.utils.Constants;

import org.joda.time.DateTime;

import java.util.List;

public class DetailsFragment extends Fragment {
    static DetailsFragment instance = null;

    ListView lvDFBatteryDetailsList;
    ProgressBar pbDFProgressBar;


    Context mCtx;
    DateTime dateTime;
    private ImageButton ibPrev;
    private TextView tvDate;
    private ImageButton ibNext;

    public DetailsFragment() {
    }

    public static DetailsFragment getInstance() {
        if (instance == null) {
            instance = new DetailsFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.details_fragment, container, false);
        mCtx = container.getContext();

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

    private void fetchDataForDate() {
        tvDate.setText(dateTime.toString(Constants.DATE_FORMAT));
        pbDFProgressBar.setVisibility(View.VISIBLE);
        BatteryEntry.DBCommands.getBatteryEntriesFor(MDB.getInstance(mCtx).getReadableDatabase(),
                dateTime,
                new BatteryEntry.GetAllBatteryEntriesCallback() {
                    @Override
                    public void onCompleted(List<BatteryEntry> l) {
                        pbDFProgressBar.setVisibility(View.GONE);
                        if (l.size() == 0) {
                            String text = tvDate.getText().toString();
                            text = text + " NO DATA";
                            tvDate.setText(text);
                            return;
                        }
                        BatteryEntryDetailsListAdapter
                                adapter = new BatteryEntryDetailsListAdapter(mCtx, l);
                        lvDFBatteryDetailsList.setAdapter(adapter);
                    }
                });
    }

    private void initViews(View v) {
        ibPrev = (ImageButton) v.findViewById(R.id.ibPrev);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        ibNext = (ImageButton) v.findViewById(R.id.ibNext);
        lvDFBatteryDetailsList = v.findViewById(R.id.lvDFBatteryDetailsList);
        pbDFProgressBar = (ProgressBar) v.findViewById(R.id.pbDFProgressBar);
    }
}
