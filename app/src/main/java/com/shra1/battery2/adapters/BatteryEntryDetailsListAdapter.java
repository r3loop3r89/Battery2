package com.shra1.battery2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shra1.battery2.R;
import com.shra1.battery2.mymodels.BatteryEntry;
import com.shra1.battery2.utils.Constants;

import org.joda.time.DateTime;

import java.util.List;

public class BatteryEntryDetailsListAdapter extends BaseAdapter {
    Context mCtx;
    List<BatteryEntry> l;

    public BatteryEntryDetailsListAdapter(Context mCtx, List<BatteryEntry> l) {
        this.mCtx = mCtx;
        this.l = l;
    }


    @Override
    public int getCount() {
        return l.size();
    }

    @Override
    public Object getItem(int position) {
        return l.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        BEDLAViewHolder h;
        if (v == null) {
            v = LayoutInflater.from(mCtx).inflate(R.layout.battery_entry_list_item_layout, parent, false);
            h=new BEDLAViewHolder(v);
            v.setTag(h);
        }else{
            h= (BEDLAViewHolder) v.getTag();
        }
        BatteryEntry b = (BatteryEntry) getItem(position);
        DateTime dateTime = new DateTime(b.getBatteryEntryOn());
        h.tvBELILBatteryEntryOn.setText(dateTime.toString(Constants.DATE_TIME_FORMAT));
        h.tvBELILBatteryLevel.setText(b.getBatteryLevel() + " %");
        return v;
    }

    class BEDLAViewHolder {
        private TextView tvBELILBatteryEntryOn;
        private TextView tvBELILBatteryLevel;
        public BEDLAViewHolder(View v) {
            tvBELILBatteryEntryOn = (TextView) v.findViewById(R.id.tvBELILBatteryEntryOn);
            tvBELILBatteryLevel = (TextView) v.findViewById(R.id.tvBELILBatteryLevel);
        }
    }
}
