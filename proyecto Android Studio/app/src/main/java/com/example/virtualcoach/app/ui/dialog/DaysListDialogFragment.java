package com.example.virtualcoach.app.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.util.DaysOfWeekUtils;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.List;

public class DaysListDialogFragment extends DialogFragment {

    private final boolean[] selectedItems;
    private final ListDialogListener listener;

    public DaysListDialogFragment(List<DayOfWeek> selectedItems, ListDialogListener listener) {
        this.selectedItems = DaysOfWeekUtils.getBooleanArray(selectedItems);

        this.listener = listener;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona días")
                .setMultiChoiceItems(R.array.dias_semana, selectedItems,
                        (dialog, which, isChecked) -> selectedItems[which] = isChecked)

                .setPositiveButton(R.string.ok, (dialog, which) -> listener.onDialogPositiveClick(this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> listener.onDialogNegativeClick(this));

        return builder.create();
    }

    public List<DayOfWeek> getSelectedDays() {
        return DaysOfWeekUtils.from(selectedItems);
    }

    public interface ListDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}