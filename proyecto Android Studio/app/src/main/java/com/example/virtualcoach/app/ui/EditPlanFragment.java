package com.example.virtualcoach.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.adapter.SessionListAdapter;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.usermanagement.di.UserManagementModule.UserManagement.CurrentUser;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditPlanFragment extends Fragment {

    private static final DateTimeFormatter SESSION_ITEM_TIME_FORMATTER = EditSessionFragment.TIMEPICKER_FORMATTER;

    @Inject
    @Nullable
    @CurrentUser
    DisplayUser currentUser;

    private EditPlanViewModel viewModel;

    private ImageButton addButton;
    private SessionListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_plan, container, false);

        addButton = view.findViewById(R.id.btn_add_session);

        recyclerView = view.findViewById(R.id.list_edit_sessions);
        emptyLabel = view.findViewById(R.id.edit_plan_sessions_empty);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();
        setupEventHandlers();
    }

    private void initComponents() {

        viewModel = new ViewModelProvider(requireActivity()).get(EditPlanViewModel.class);

        adapter = new SessionListAdapter(SESSION_ITEM_TIME_FORMATTER);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void setupEventHandlers() {
        View.OnClickListener onAddButtonClick = v -> {
            viewModel.selectEmpty();
            NavHostFragment.findNavController(this).navigate(
                    EditPlanFragmentDirections.actionEditPlanFragmentToEditSessionFragment());
        };
        SessionListAdapter.OnItemClickListener onItemClick = (session, position) -> {
            viewModel.select(session);
            NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToEditSessionFragment();
            NavHostFragment.findNavController(EditPlanFragment.this).navigate(action);
        };
        SessionListAdapter.OnItemRemoveClickListener onItemRemoveClickListener = position -> {
            TrainingSession session = adapter.get(position);

            new AlertDialog.Builder(EditPlanFragment.this.requireContext())
                    .setTitle("Borrar sesión")
                    .setMessage("¿Estas seguro de que quieres borrar la sesion?")
                    .setPositiveButton("Si", (dialog, which) -> viewModel.removeSession(session))
                    .setNegativeButton("No", (dialog, which) -> {
                    })
                    .create()
                    .show();
        };

        viewModel.getSessionsFromUser(currentUser).observe(getViewLifecycleOwner(), trainingSessions -> {
            adapter.setData(trainingSessions);
            updateUI();
        });

        addButton.setOnClickListener(onAddButtonClick);

        adapter.setOnItemClickListener(onItemClick);
        adapter.setOnItemRemoveClickListener(onItemRemoveClickListener);
    }

    private void updateUI() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyLabel.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLabel.setVisibility(View.GONE);
        }
    }
}