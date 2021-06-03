package com.example.virtualcoach.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.util.StringUtils;
import com.example.virtualcoach.database.data.model.Exercise;

import java.util.List;
import java.util.Locale;

public class ExercisePickerListAdapter extends RecyclerView.Adapter<ExercisePickerListAdapter.ViewHolder> {
    private final List<Exercise> exercises;
    private final OnPickItem onPickItemListener;

    public ExercisePickerListAdapter(List<Exercise> exercises, OnPickItem onPickItemListener) {
        this.exercises = exercises;
        this.onPickItemListener = onPickItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_select_exercise, parent, false);

        return new ViewHolder(view, position -> onPickItemListener.onPick(exercises.get(position)));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExercise(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView difficulty;
        private final TextView duration;
        private final TextView steps;
        private final TextView avgHR;

        public ViewHolder(@NonNull View itemView, ViewHolderListeners listeners) {
            super(itemView);

            name = itemView.findViewById(R.id.picker_item_exercise_name);
            difficulty = itemView.findViewById(R.id.picker_item_exercise_difficulty);
            duration = itemView.findViewById(R.id.picker_item_exercise_duration);
            steps = itemView.findViewById(R.id.picker_item_exercise_steps);
            avgHR = itemView.findViewById(R.id.picker_item_exercise_avghr);

            itemView.setOnClickListener(v -> listeners.onItemClick(getAdapterPosition()));
        }

        public void setExercise(Exercise exercise) {
            name.setText(exercise.name);
            difficulty.setText(String.format("(%s)", StringUtils.capitalize(exercise.difficulty)));
            duration.setText(String.format(Locale.US, "%d min", exercise.duration));
            steps.setText(String.format(Locale.US, "Pasos: %d", exercise.steps));
            avgHR.setText(String.format(Locale.US, "FC Media: %d", exercise.avgHR));
        }
    }

    interface ViewHolderListeners {
        void onItemClick(int position);
    }

    public interface OnPickItem {
        void onPick(Exercise exercise);
    }
}
