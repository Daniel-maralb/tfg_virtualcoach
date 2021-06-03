package com.example.virtualcoach.app.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.database.data.model.TrainingSession;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.ViewHolder> {

    private final DateTimeFormatter timeFormatter;

    private List<TrainingSession> trainingSessions;
    private int deleteSessionButtonVisibility;

    private OnItemClickListener onItemClickListener;
    private OnItemRemoveClickListener onItemRemoveClickListener;

    public SessionListAdapter(DateTimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
        this.deleteSessionButtonVisibility = View.GONE;
        this.trainingSessions = Collections.emptyList();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemRemoveClickListener(OnItemRemoveClickListener onItemRemoveClickListener) {
        this.onItemRemoveClickListener = onItemRemoveClickListener;
    }

    public void setData(List<TrainingSession> trainingSessions) {
        this.trainingSessions = trainingSessions;
        notifyDataSetChanged();
    }

    public void add(TrainingSession session) {
        trainingSessions.add(session);
        notifyDataSetChanged();

    }

    public TrainingSession get(int position) {
        return trainingSessions.get(position);
    }

    public void remove(TrainingSession session) {
        trainingSessions.remove(session);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_edit_session, parent, false);

        return new ViewHolder(view, timeFormatter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainingSession session = trainingSessions.get(position);

        holder.setSession(session);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(session, position));
        holder.itemView.setOnLongClickListener(v -> {
            onItemRemoveClickListener.onItemRemoveClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return trainingSessions.size();
    }

    public List<TrainingSession> getData() {
        return trainingSessions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView sessionLabel;
        final TextView descriptionLabel;

        private final Resources resources;

        private final DateTimeFormatter timeFormatter;

        public ViewHolder(@NonNull View itemView, DateTimeFormatter formatter) {
            super(itemView);
            this.resources = itemView.getResources();
            this.timeFormatter = formatter;

            this.sessionLabel = itemView.findViewById(R.id.row_item_exercise_name);
            this.descriptionLabel = itemView.findViewById(R.id.row_item_session_description);
        }

        public void setSession(TrainingSession session) {

            String time = isNull(session.startTime) ?
                    resources.getString(R.string.default_time_picker_value)
                    : session.getStartTime().format(timeFormatter);

            String repetition =
                    isNull(session.repetition) ?
                            resources.getString(R.string.default_days_picker_value) :
                            session.repetition;

            descriptionLabel.setText(String.format(Locale.US, "%s a las %s", repetition, time));

            sessionLabel.setText(String.format(Locale.US, "Sesión %d", getSessionNumber()));
        }

        private Integer getSessionNumber() {
            return getAdapterPosition() + 1;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TrainingSession session, Integer position);
    }

    public interface OnItemRemoveClickListener {
        void onItemRemoveClick(int possition);
    }
}
