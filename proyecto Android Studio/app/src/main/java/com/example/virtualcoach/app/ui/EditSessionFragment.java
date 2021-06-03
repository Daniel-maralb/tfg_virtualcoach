package com.example.virtualcoach.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.adapter.SessionExercisesListAdapter;
import com.example.virtualcoach.app.ui.dialog.DaysListDialogFragment;
import com.example.virtualcoach.app.ui.dialog.ExercisePickerDialogFragment;
import com.example.virtualcoach.app.ui.dialog.TimePickerDialogFragment;
import com.example.virtualcoach.app.util.DaysOfWeekUtils;
import com.example.virtualcoach.app.util.NullUtils;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionWithExercises;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.recommendations.RecommendationManager;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@AndroidEntryPoint
public class EditSessionFragment extends Fragment {

    public static final DateTimeFormatter TIMEPICKER_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String STATE_KEY_MODIFIED = "modified";

    private boolean paused;
    private boolean isModified;
    private TrainingSession session;

    private EditPlanViewModel viewModel;
    private NavController navController;

    private ProgressBar loadingBar;
    private View content;

    private ActionBar actionBar;
    private SessionExercisesListAdapter adapter;
    private ImageButton addButton;
    private RecyclerView recyclerView;
    private EditText timePicker;
    private EditText repetitionPicker;
    private Runnable saveChangesAndNavigateUpAction;

    private ExercisePickerDialogFragment exercisePickerDialogFragment;
    private TimePickerDialogFragment timePickerDialogFragment;
    private DaysListDialogFragment daysListDialogFragment;

    @Inject
    RecommendationManager recommendationManager;
    @Inject
    UserRepository userRepository;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.confirm_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem confirm = menu.findItem(R.id.menu_action_confirm);

        confirm.setVisible(isModified && isSessionComplete());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_action_confirm) {
            saveChangesAndNavigateUpAction.run();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isModified && isSessionComplete()) {
                    navController.popBackStack();
                    return;
                }
                getSaveChangesDialog().show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        handlePrevState(savedInstanceState);

        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;

        if (!isNull(exercisePickerDialogFragment))
            exercisePickerDialogFragment.dismiss();

        if (!isNull(timePickerDialogFragment))
            timePickerDialogFragment.dismiss();

        if (!isNull(daysListDialogFragment))
            daysListDialogFragment.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_MODIFIED, isModified);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();

        View view = inflater.inflate(R.layout.fragment_edit_session, container, false);

        loadingBar = view.findViewById(R.id.edit_session_loading);
        content = view.findViewById(R.id.edit_session_content);

        timePicker = view.findViewById(R.id.input_session_start_time);
        repetitionPicker = view.findViewById(R.id.input_session_repetition);
        addButton = view.findViewById(R.id.btn_add_exercise);
        recyclerView = view.findViewById(R.id.list_edit_exercises);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();

        initUI();

        setupEventHandlers();
    }

    private void handlePrevState(Bundle savedInstanceState) {
        if (isNull(savedInstanceState))
            return;

        isModified = savedInstanceState.getBoolean(STATE_KEY_MODIFIED, false);
    }

    private void setupEventHandlers() {

        View.OnClickListener onAddButtonClick = v -> {
            List<Exercise> allExercises = viewModel.getExercises();
            List<Exercise> recommendedExercises = recommendationManager.getRecommendations(allExercises);

            if (recommendedExercises.isEmpty()) {

                DisplayUser currentUser = userRepository.getCurrentUser();
                if (NullUtils.anyNull(currentUser.age, currentUser.height, currentUser.weight))
                    Toast.makeText(getContext(), "Configure sus datos para recibir recomendaciones", Toast.LENGTH_LONG).show();

                exercisePickerDialogFragment = new ExercisePickerDialogFragment("Selecciona ejercicio", allExercises,
                        exercise -> {
                            addExercise(v, exercise);
                        });
                exercisePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "exercise-picker");
                return;
            }

            exercisePickerDialogFragment = new ExercisePickerDialogFragment("Ejercicios recomendados", recommendedExercises,
                    exercise -> {
                        addExercise(v, exercise);
                    })
                    .setOnDismissListener(dialog -> {
                        if (!paused) {
                            exercisePickerDialogFragment = new ExercisePickerDialogFragment("Selecciona ejercicio", allExercises,
                                    exercise -> {
                                        addExercise(v, exercise);
                                    });
                            exercisePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "exercise-picker");
                        }
                    });
            exercisePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "recommended-exercise-picker");
        };
        View.OnClickListener onTimePickerClick = v -> {
            timePickerDialogFragment = new TimePickerDialogFragment((view, hourOfDay, minute) -> {
                LocalTime time = LocalTime.of(hourOfDay, minute);

                if (time.equals(session.getStartTime()))
                    return;

                session.setStartTime(time);
                setModified(true);

                timePicker.setText(getTimePickerText(session));

            });
            timePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "session-time");
        };

        View.OnClickListener onRepetitionPickerClick = v -> {
            daysListDialogFragment = new DaysListDialogFragment(session.getRepetition(), new DaysListDialogFragment.ListDialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    List<DayOfWeek> days = ((DaysListDialogFragment) dialog).getSelectedDays();

                    if (DaysOfWeekUtils.equals(days, session.getRepetition()))
                        return;

                    session.setRepetition(days);
                    setModified(true);

                    repetitionPicker.setText(getRepetitionText(session));
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                }
            });
            daysListDialogFragment.show(EditSessionFragment.this.requireActivity().getSupportFragmentManager(), "session-repetition");
        };
        SessionExercisesListAdapter.OnMoveButtonsClickListener onMoveButtonsClickListener = new SessionExercisesListAdapter.OnMoveButtonsClickListener() {
            @Override
            public void onMoveItemUp(int position) {
                adapter.moveItemUp(position);
                EditSessionFragment.this.setModified(true);
            }

            @Override
            public void onMoveItemDown(int position) {
                adapter.moveItemDown(position);
                EditSessionFragment.this.setModified(true);
            }
        };
        SessionExercisesListAdapter.OnRemoveExerciseClickListener onRemoveExerciseClick = position -> {
            adapter.remove(position);
            setModified(true);
        };
        FutureCallback<TrainingSessionWithExercises> onSessionChanged = new FutureCallback<TrainingSessionWithExercises>() {
            @Override
            public void onSuccess(@NullableDecl TrainingSessionWithExercises sessionWithExercises) {
                assert sessionWithExercises != null;

                setData(sessionWithExercises);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Error inicializando la sesión", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(EditSessionFragment.this).popBackStack();
            }
        };

        saveChangesAndNavigateUpAction = () -> {
            viewModel.save(new TrainingSessionWithExercises(session, adapter.getData()));
            navController.popBackStack();
        };

        adapter.setOnRemoveExerciseListener(onRemoveExerciseClick);
        adapter.setOnMoveButtonsClickListener(onMoveButtonsClickListener);

        addButton.setOnClickListener(onAddButtonClick);

        timePicker.setOnClickListener(onTimePickerClick);

        repetitionPicker.setOnClickListener(onRepetitionPickerClick);

        Futures.addCallback(viewModel.getSelected(), onSessionChanged, viewModel.getDbExecutor());
    }

    private void addExercise(View v, Exercise exercise) {
        if (adapter.getData().contains(exercise)) {
//                            Toast.makeText(getContext(), R.string.snack_msg_exercise_already_in_session,Toast.LENGTH_LONG).show();
            Snackbar.make(v, R.string.snack_msg_exercise_already_in_session, Snackbar.LENGTH_LONG).show();
            return;
        }
        adapter.add(exercise);
        setModified(true);
    }

    private void updateUI() {

        if (!isNull(actionBar))
            actionBar.setTitle(getTitleText());

        if (isNull(session)) {
            loadingBar.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            return;
        }

        loadingBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        timePicker.setText(
                isNull(session.startTime) ?
                        getString(R.string.default_time_picker_value) :
                        getTimePickerText(session)
        );

        repetitionPicker.setText(
                TextUtils.isEmpty(session.repetition) ?
                        getString(R.string.default_days_picker_value) :
                        TextUtils.join("-", Lists.charactersOf(session.repetition))
        );
    }

    private void initComponents() {

        paused = false;

        viewModel = new ViewModelProvider(requireActivity()).get(EditPlanViewModel.class);

        adapter = new SessionExercisesListAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        daysListDialogFragment = null;
        timePickerDialogFragment = null;
        exercisePickerDialogFragment = null;
    }

    private String getTitleText() {
        if (isNull(session))
            return getString(R.string.destination_edit_session_loading_title);

        return getString(R.string.destination_edit_session_title);
    }

    private String getTimePickerText(TrainingSession session) {
        LocalTime time = session.getStartTime();

        return isNull(time) ?
                getString(R.string.default_time_picker_value) :
                time.format(TIMEPICKER_FORMATTER);
    }

    private String getRepetitionText(TrainingSession session) {
        String repetition = session.repetition;

        if (isNull(repetition) || repetition.isEmpty())
            return getString(R.string.default_days_picker_value);

        return TextUtils.join("-", Lists.charactersOf(repetition));
    }

    private void setModified(boolean enabled) {

        isModified = enabled;

        requireActivity().invalidateOptionsMenu();
    }

    private AlertDialog getSaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("Guardar cambios")
                .setMessage("La sesión se ha modificado, ¿desea guardar los cambios antes de salir?.")
                .setNeutralButton("Volver", (dialog, which) -> {
                })
                .setNegativeButton("Descartar", (dialog, which) -> navController.popBackStack());

        if (isSessionComplete())
            builder.setPositiveButton("Guardar", (dialog, which) -> saveChangesAndNavigateUpAction.run());

        return builder
                .create();
    }

    private boolean isSessionComplete() {
        return !isNull(session) && !TextUtils.isEmpty(session.repetition) && !isNull(session.startTime);
    }

    private void setData(TrainingSessionWithExercises sessionWithExercises) {
        session = sessionWithExercises.session;
        adapter.setData(new ArrayList<>(sessionWithExercises.exercises));

        updateUI();
    }

    private void initUI() {
        try {
            setData(viewModel.getSelected().get(1000, TimeUnit.MILLISECONDS));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException ignored) {
            updateUI();
        }
    }
}