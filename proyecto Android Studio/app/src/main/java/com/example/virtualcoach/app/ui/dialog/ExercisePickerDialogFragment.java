package com.example.virtualcoach.app.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.adapter.ExercisePickerListAdapter;
import com.example.virtualcoach.database.data.model.Exercise;

import java.util.Collections;
import java.util.List;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

public class ExercisePickerDialogFragment extends DialogFragment {
    private final List<Exercise> exercises;
    private final OnItemSelectedListener onItemSelectedListener;
    private DialogInterface.OnDismissListener onDismissListener;

    private final String dialogTitle;

    private boolean done;

    public ExercisePickerDialogFragment() {
        exercises = Collections.emptyList();
        onItemSelectedListener = e -> {
        };
        dialogTitle = "";
    }

    public ExercisePickerDialogFragment(String title,
                                        List<Exercise> exercises,
                                        OnItemSelectedListener onItemSelectedListener) {
        dialogTitle = title;
        this.exercises = exercises;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        done = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(dialogTitle)
                .setView(getCustomView(getContext()));

        return builder.create();
    }

    private View getCustomView(Context context) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.list_select_exercise, null);

        RecyclerView recyclerView = view.findViewById(R.id.list_select_exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ExercisePickerListAdapter(exercises, e -> {
            onItemSelectedListener.onItemSelected(e);
            done = true;
            dismiss();
        }));

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!done && !isNull(onDismissListener))
            onDismissListener.onDismiss(dialog);

        super.onDismiss(dialog);
    }

    public ExercisePickerDialogFragment setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Exercise e);
    }
}
