package com.example.virtualcoach.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.database.data.model.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SessionExercisesListAdapter extends RecyclerView.Adapter<SessionExercisesListAdapter.ViewHolder> {


    private List<Exercise> exercises;
    private int deleteExerciseButtonVisibility;
    private OnRemoveExerciseClickListener onRemoveExerciseListener;
    private OnMoveButtonsClickListener onMoveButtonsClick;

    public SessionExercisesListAdapter() {
        deleteExerciseButtonVisibility = View.GONE;
        exercises = new ArrayList<>();
    }

    public void setData(List<Exercise> data) {
        exercises = data;
        notifyDataSetChanged();
    }

    public List<Exercise> getData() {
        return exercises;
    }

    public void remove(int position) {
        exercises.remove(position);
        notifyDataSetChanged();
    }

    public void add(Exercise exercise) {
        exercises.add(exercise);
        notifyDataSetChanged();
    }

    public void moveItemUp(int position) {
        int dstPos = position - 1;
        if (dstPos < 0)
            return;
        swapExercises(position, dstPos);
        notifyItemMoved(position, dstPos);
        notifyItemChanged(position);
        notifyItemChanged(dstPos);
    }

    public void moveItemDown(int position) {
        int dstPos = position + 1;
        if (dstPos >= exercises.size())
            return;
        swapExercises(position, dstPos);
        notifyItemMoved(position, dstPos);
        notifyItemChanged(position);
        notifyItemChanged(dstPos);
    }

    private void swapExercises(int pos1, int pos2) {
        Exercise aux = exercises.get(pos1);
        exercises.set(pos1, exercises.get(pos2));
        exercises.set(pos2, aux);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_edit_exercise, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.setExercise(exercise);
        holder.setMoveButtonsVisibility(position > 0, position < exercises.size() - 1);
        holder.setOnMoveButtonsClickListener(onMoveButtonsClick);
        holder.itemView.setOnLongClickListener(v -> {
            onRemoveExerciseListener.onRemoveExercise(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void setOnRemoveExerciseListener(OnRemoveExerciseClickListener onRemoveExerciseListener) {
        this.onRemoveExerciseListener = onRemoveExerciseListener;
    }

    public void setOnMoveButtonsClickListener(OnMoveButtonsClickListener onMoveButtonsClick) {
        this.onMoveButtonsClick = onMoveButtonsClick;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameLabel;
        private final TextView difficultyLabel;
        private final TextView timeLabel;
        private final ImageButton moveUpButton;
        private final ImageButton moveDownButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameLabel = itemView.findViewById(R.id.row_item_exercise_name);
            difficultyLabel = itemView.findViewById(R.id.row_item_exercise_difficulty);
            moveUpButton = itemView.findViewById(R.id.row_item_exercise_move_up);
            moveDownButton = itemView.findViewById(R.id.row_item_exercise_move_down);
            timeLabel = itemView.findViewById(R.id.row_item_exercise_time);
        }

        public void setExercise(Exercise exercise) {
            nameLabel.setText(exercise.name);
            difficultyLabel.setText(exercise.difficulty);
            timeLabel.setText(String.format(Locale.US, "%d min", exercise.duration));
        }

        public void setMoveButtonsVisibility(boolean upIsVisible, boolean downIsVisible) {
            if (upIsVisible)
                moveUpButton.setVisibility(View.VISIBLE);
            else
                moveUpButton.setVisibility(View.INVISIBLE);

            if (downIsVisible)
                moveDownButton.setVisibility(View.VISIBLE);
            else
                moveDownButton.setVisibility(View.INVISIBLE);
        }

        public void setOnMoveButtonsClickListener(OnMoveButtonsClickListener onMoveButtonsClick) {
            moveUpButton.setOnClickListener(v -> onMoveButtonsClick.onMoveItemUp(getAdapterPosition()));
            moveDownButton.setOnClickListener(v -> onMoveButtonsClick.onMoveItemDown(getAdapterPosition()));
        }
    }

    public interface OnMoveButtonsClickListener {
        void onMoveItemUp(int position);

        void onMoveItemDown(int position);
    }

    public interface OnRemoveExerciseClickListener {
        void onRemoveExercise(int position);
    }
}
