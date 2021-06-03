package com.example.virtualcoach.app.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.adapter.TrainingListAdapter;
import com.example.virtualcoach.app.util.PermissionGuard;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrainingProgressFragment extends Fragment {

    private TrainingProgressViewModel viewModel;

    private TextView emptyListText;
    private RecyclerView sessionList;
    private TrainingListAdapter adapter;
    private boolean firstTime;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(TrainingProgressViewModel.class);

        View view = inflater.inflate(R.layout.fragment_training_progress, container, false);

        sessionList = view.findViewById(R.id.list_trainings_done);
        emptyListText = view.findViewById(R.id.session_logs_empty_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PermissionGuard checkForStorage = new PermissionGuard(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                R.string.permission_storage_rationale);

        checkForStorage.setOnPermissionDenied(() -> {
        });
        checkForStorage.request(() -> {
        });

        if (viewModel.isLoggedIn()) {
            initComponents();
            setupEventHandlers();
        }
    }

    private void initComponents() {

        adapter = new TrainingListAdapter();
        sessionList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        sessionList.setAdapter(adapter);

        emptyListText.setVisibility(View.GONE);

        firstTime = true;
    }

    private void setupEventHandlers() {
        final Observer<List<TrainingSessionLog>> onListUpdated = trainingSessionLogs -> {
            if (trainingSessionLogs.isEmpty() && adapter.getItemCount() == 0) {
                emptyListText.setVisibility(View.VISIBLE);
                return;
            }
            emptyListText.setVisibility(View.GONE);

            ArrayList<TrainingSessionLog> aux = new ArrayList<>(trainingSessionLogs);
            Collections.reverse(aux);

            adapter.setData(aux);

            if (firstTime)
                sessionList.scrollToPosition(adapter.getItemCount() - 1);

            firstTime = false;
        };

        viewModel.getCurrentUserLogs().observe(getViewLifecycleOwner(), onListUpdated);
    }
}