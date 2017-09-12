package com.farukydnn.lastearthquakes.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.farukydnn.lastearthquakes.model.Earthquake;
import com.farukydnn.lastearthquakes.interfaces.ListPageService;
import com.farukydnn.lastearthquakes.R;

public class EarthquakeAdapter extends BaseAdapter {

    private final List<Earthquake.EarthquakeData> earthquakes;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ListPageService listPageService;

    public EarthquakeAdapter(FragmentActivity context, List<Earthquake.EarthquakeData> earthquakes) {
        this.earthquakes = earthquakes;
        this.context = context;

        listPageService = (ListPageService) context;

        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return earthquakes.size();
    }

    @Override
    public Earthquake.EarthquakeData getItem(int i) {
        return earthquakes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_view_item, viewGroup, false);

            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        double severity = getItem(i).getSeverity();

        viewHolder.severity.setText(String.valueOf(severity));
        viewHolder.location.setText(getItem(i).getLocation());
        viewHolder.date.setText(getItem(i).getDate());
        viewHolder.time.setText(getItem(i).getTime());

        if (listPageService.getCurrentPageIndex() == ListPageService.PAGE_IMPORTANT_EARTHQUAKES) {
            Resources res = context.getResources();

            viewHolder.felt_severity.setText(res.getString(R.string.list_felt_severity, getItem(i).getFeltSeverity()));
            viewHolder.life_loss.setText(res.getString(R.string.list_life_loss, getItem(i).getLiseLoss()));
            viewHolder.damaged_building.setText(res.getString(R.string.list_damaged_building, getItem(i).getDamagedBuilding()));
        } else {
            viewHolder.felt_severity.setVisibility(View.GONE);
            viewHolder.life_loss.setVisibility(View.GONE);
            viewHolder.damaged_building.setVisibility(View.GONE);
        }

        Drawable severityBackgroundImage = ContextCompat.getDrawable(context, R.drawable.earthquake_severity_background);
        severityBackgroundImage = DrawableCompat.wrap(severityBackgroundImage);


        int severityColor;

        if (severity < 4)
            severityColor = ContextCompat.getColor(context, R.color.earthquakeSeverityLow);
        else if (severity < 6)
            severityColor = ContextCompat.getColor(context, R.color.earthquakeSeverityMedium);
        else
            severityColor = ContextCompat.getColor(context, R.color.earthquakeSeverityHigh);

        DrawableCompat.setTint(severityBackgroundImage, severityColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            viewHolder.severity.setBackground(severityBackgroundImage);
        else
            viewHolder.severity.setBackgroundDrawable(severityBackgroundImage);

        return view;
    }

    private static class ViewHolder {
        private final TextView severity, location, date, time,
                felt_severity, life_loss, damaged_building;

        private ViewHolder(View view) {
            severity = view.findViewById(R.id.earthquake_severity);
            location = view.findViewById(R.id.earthquake_location);
            date = view.findViewById(R.id.earthquake_date);
            time = view.findViewById(R.id.earthquake_time);
            felt_severity = view.findViewById(R.id.earthquake_felt_severity);
            life_loss = view.findViewById(R.id.earthquake_life_loss);
            damaged_building = view.findViewById(R.id.earthquake_damaged_building);
        }
    }
}
