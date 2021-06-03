package com.example.virtualcoach.app.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class TrainingListAdapter extends RecyclerView.Adapter<TrainingListAdapter.ViewHolder> {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm 'del' cccc d LLLL");

    @NonNull
    private List<TrainingSessionLog> data;

    public TrainingListAdapter() {
        data = Collections.emptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_session_log, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(@NonNull List<TrainingSessionLog> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView sessionDate;
        private final TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sessionDate = itemView.findViewById(R.id.row_item_session_log_date);
            comment = itemView.findViewById(R.id.row_item_session_log_results);

        }

        public void setData(TrainingSessionLog sessionLog) {
            sessionDate.setText(LocalDateTime.ofInstant(sessionLog.getStartTime(), ZoneOffset.UTC).format(DATE_TIME_FORMATTER));

            if (!TextUtils.isEmpty(sessionLog.comment))
                comment.setText(sessionLog.comment);
        }
    }
}
