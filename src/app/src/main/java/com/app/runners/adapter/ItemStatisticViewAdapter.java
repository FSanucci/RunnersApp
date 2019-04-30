package com.app.runners.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.runners.R;
import com.app.runners.model.StatisticItem;

import java.text.SimpleDateFormat;
import java.util.List;

public class ItemStatisticViewAdapter extends RecyclerView.Adapter<ItemStatisticViewAdapter.ViewHolder> {

    private final Context mContext;
    private final List<StatisticItem> mValues;

    public ItemStatisticViewAdapter(List<StatisticItem> items, Context newContext) {
        mValues = items;
        mContext = newContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_statistic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (holder.mItem.startList){
            String separator = mContext.getString(R.string.statistics_distance_total) + ": " + holder.mItem.race.km + " " + mContext.getString(R.string.statistics_distance);
            holder.mItemSpace.setVisibility(View.VISIBLE);
            holder.mSeparator.setVisibility(View.VISIBLE);
            holder.mSeparator.setText(separator);
        } else {
            holder.mItemSpace.setVisibility(View.GONE);
            holder.mSeparator.setVisibility(View.GONE);
        }

        if (holder.mItem.race.raceName == null || holder.mItem.race.raceName.isEmpty()){
            holder.mActivity_name.setVisibility(View.GONE);
        } else {
            holder.mActivity_name.setVisibility(View.VISIBLE);
            holder.mActivity_name.setText(holder.mItem.race.raceName);
        }

        if (holder.mItem.race.km == null || holder.mItem.race.km <= 0 ){
            holder.mActivity_distance.setVisibility(View.GONE);
        } else {
            String distance = holder.mItem.race.km + mContext.getString(R.string.statistics_distance);
            holder.mActivity_distance.setVisibility(View.VISIBLE);
            holder.mActivity_distance.setText(distance);
        }

        if (holder.mItem.race.runningDate == null){
            holder.mActivity_date.setVisibility(View.GONE);
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = fmt.format(holder.mItem.race.runningDate);
            if (dateStr != null && !dateStr.isEmpty()){
                holder.mActivity_date.setVisibility(View.VISIBLE);
                holder.mActivity_date.setText(dateStr);
            } else {
                holder.mActivity_date.setVisibility(View.GONE);
            }
        }

        if (holder.mItem.duration == null || holder.mItem.duration == 0){
            holder.mDuration_total.setVisibility(View.VISIBLE);
            holder.mDuration_total.setText(mContext.getString(R.string.statistics_empty_data));
        } else {

            String duration = "";
            long minutesTotal = holder.mItem.duration;
            long hoursTotal = minutesTotal / 60;
            long daysTotal = hoursTotal / 24;
            hoursTotal = hoursTotal - (daysTotal * 24);
            minutesTotal = minutesTotal - (daysTotal * 24 * 60) - (hoursTotal * 60);
            if (daysTotal > 0){
                duration = String.valueOf(daysTotal) + mContext.getString(R.string.statistics_duration_days) + " " + String.valueOf(hoursTotal) + mContext.getString(R.string.statistics_duration_hour) + " " + String.valueOf(minutesTotal) + mContext.getString(R.string.statistics_duration_min);
            } else if (hoursTotal > 0){
                duration = String.valueOf(hoursTotal) + mContext.getString(R.string.statistics_duration_hour) + " " + String.valueOf(minutesTotal) + mContext.getString(R.string.statistics_duration_min);
            } else {
                duration = String.valueOf(minutesTotal) + mContext.getString(R.string.statistics_duration_min);
            }

            holder.mDuration_total.setVisibility(View.VISIBLE);
            holder.mDuration_total.setText(duration);
        }

        if (holder.mItem.race.km != null && holder.mItem.race.km > 0 && holder.mItem.duration != null && holder.mItem.duration >= 0){
            float minutesTotal = (float) holder.mItem.duration;
            float speed = minutesTotal / holder.mItem.race.km;

            int speedMinutes = (int) speed;
            int speedSeconds = (int) ((speed - (int) speed) * 60);
            String secondsFormat = String.format("%02d", speedSeconds);
            String speedStr = speedMinutes + "’ " + secondsFormat + "’’";


            //String speedStr = String.format("%.2f", speed) + mContext.getString(R.string.statistics_speed);
            holder.mSpeed_total.setVisibility(View.VISIBLE);
            holder.mSpeed_total.setText(speedStr);
        } else {
            holder.mSpeed_total.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mActivity_name;
        public final TextView mActivity_distance;
        public final TextView mActivity_date;
        public final TextView mDuration_total;
        public final TextView mSpeed_total;
        public final TextView mSeparator;
        public final LinearLayout mItemSpace;
        public StatisticItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mActivity_name = (TextView) view.findViewById(R.id.activity_name);
            mActivity_distance = (TextView) view.findViewById(R.id.activity_distance);
            mActivity_date = (TextView) view.findViewById(R.id.activity_date);
            mDuration_total = (TextView) view.findViewById(R.id.duration_total);
            mSpeed_total = (TextView) view.findViewById(R.id.speed_total);
            mSeparator = (TextView) view.findViewById(R.id.separator);
            mItemSpace = (LinearLayout) view.findViewById(R.id.item_row_space);
        }

    }
}
