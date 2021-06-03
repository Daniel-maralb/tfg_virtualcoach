package com.example.virtualcoach.sessionlog;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionWithExercises;
import com.example.virtualcoach.database.data.repository.DefaultActivityDataRepository;
import com.example.virtualcoach.database.data.repository.ExerciseRepository;
import com.example.virtualcoach.database.data.repository.TrainingSessionLogRepository;
import com.example.virtualcoach.database.data.repository.TrainingSessionRepository;
import com.example.virtualcoach.usermanagement.di.UserManagementModule.UserManagement.CurrentUser;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UpdateSessionLogsTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSessionLogsTask.class);
    public static final int HRDIFF_TOO_INTENSE = 5;
    public static final int HRDIFF_NOT_INTENSE_ENOUGH = 5;

    private final TrainingSessionRepository sessionRepository;
    private final TrainingSessionLogRepository logRepository;
    private final DefaultActivityDataRepository activityDataRepository;
    private final ExerciseRepository exerciseRepository;
    private final DisplayUser currentUser;

    @Inject
    public UpdateSessionLogsTask(TrainingSessionRepository sessionRepository,
                                 TrainingSessionLogRepository logRepository,
                                 DefaultActivityDataRepository activityDataRepository,
                                 ExerciseRepository exerciseRepository,
                                 @Nullable @CurrentUser DisplayUser currentUser) {
        this.sessionRepository = sessionRepository;
        this.logRepository = logRepository;
        this.activityDataRepository = activityDataRepository;
        this.exerciseRepository = exerciseRepository;
        this.currentUser = Objects.requireNonNull(currentUser);
    }

    @Override
    public void run() {
        LOG.debug("Updating session logs");

        List<TrainingSession> sessions = sessionRepository.getAllFromUserSync(currentUser);
        List<TrainingSessionLog> logs = new ArrayList<>();

        for (TrainingSession session : sessions) {

            logs.addAll(getLogsOfSession(
                    new TrainingSessionWithExercises(session,
                            exerciseRepository.getFromSessionSync(session))));
        }

        saveLogs(logs);
        sessionRepository.updateAllSync(sessions);

        LOG.debug("Session logs updated");
    }

    private List<TrainingSessionLog> getLogsOfSession(TrainingSessionWithExercises sessionWithExercises) {
        List<TrainingSessionLog> result = new ArrayList<>();

        List<Pair<LocalDateTime, List<Pair<Exercise, ExerciseLog>>>> allSessions = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        LocalTime time = sessionWithExercises.session.getStartTime();

        LocalDateTime start = LocalDateTime.ofInstant(
                sessionWithExercises.session.getModificationTime(), ZoneOffset.UTC)
                .withHour(time.getHour())
                .withMinute(time.getMinute());

        LOG.debug("Updating logs of: " +
                time.format(DateTimeFormatter.ofPattern("HH:mm"))
                + " Days: " + sessionWithExercises.session.repetition + " Last Mod: " + start);

        LocalDateTime max = start;
        while (start.isBefore(now)) {

            if (start.isAfter(max))
                max = start;

            LocalDateTime sessionStart;
            LocalDateTime sessionEnd;
            LocalDateTime exerciseStart;

            for (DayOfWeek d : sessionWithExercises.session.getRepetition()) {
                sessionStart = start.with(TemporalAdjusters.next(d));
                sessionEnd = LocalDateTime.from(sessionStart);

                for (Exercise e : sessionWithExercises.exercises) {
                    sessionEnd = sessionEnd.plus(e.getDuration());
                }

                if (!sessionEnd.isBefore(now))
                    break;

                exerciseStart = LocalDateTime.from(sessionStart);

                List<Pair<Exercise, ExerciseLog>> sessionData = new ArrayList<>();
                for (Exercise e : sessionWithExercises.exercises) {

                    sessionData.add(Pair.create(e, getExerciseData(exerciseStart, e)));

                    //Asume sorted by position
                    exerciseStart = exerciseStart.plus(e.getDuration());
                }
                allSessions.add(Pair.create(sessionStart, sessionData));
            }
            start = start.plus(1, ChronoUnit.WEEKS);
        }

        List<String> observations = new ArrayList<>();

        for (Pair<LocalDateTime, List<Pair<Exercise, ExerciseLog>>> sessionWithLogs : allSessions) {
            observations.clear();

            for (Pair<Exercise, ExerciseLog> log : sessionWithLogs.second) {
                observations.addAll(getExerciseObservations(log));
            }
            result.add(new TrainingSessionLog(currentUser.id, sessionWithLogs.first.toInstant(ZoneOffset.UTC),
                    TextUtils.join("\n", observations)));
        }

        sessionWithExercises.session.setModificationTime(max.toInstant(ZoneOffset.UTC));

        return result;
    }

    private List<String> getExerciseObservations(Pair<Exercise, ExerciseLog> p) {
        List<String> observations = new ArrayList<>();
        String exerciseName;
        int stepsDiff;
        int hrDiff;
        exerciseName = p.first.name + "(" + p.first.difficulty + ")";

        if (p.second.steps == 0 && p.second.avgHR == 0)
            return List.of(exerciseName + ": No hay datos.");

        stepsDiff = p.first.steps - p.second.steps;
        hrDiff = p.first.avgHR - p.second.avgHR;

        if (hrDiff > HRDIFF_NOT_INTENSE_ENOUGH)
            observations.add(exerciseName + ": FCMed(" + -hrDiff + ") Intensidad insuficiente.");
        else if (hrDiff < -HRDIFF_TOO_INTENSE)
            observations.add(exerciseName + ": FCMed(+" + -hrDiff + ") Demasiada intensidad.");

        if (stepsDiff > 0)
            observations.add(exerciseName + ": Han faltado " + stepsDiff + " pasos.");

        if (observations.isEmpty())
            observations.add(exerciseName + ": Objetivo conseguido.");

        return observations;
    }

    private ExerciseLog getExerciseData(LocalDateTime start, Exercise e) {

        return new ExerciseLog(
                getStepsInActivity(start, e.getDuration()),
                getAvgHRInActivity(start, e.getDuration()),
                start);
    }

    private int getAvgHRInActivity(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        return activityDataRepository.getAvgHRSync(
                start.toInstant(ZoneOffset.UTC),
                end.toInstant(ZoneOffset.UTC));
    }

    private int getStepsInActivity(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        return activityDataRepository.getStepsSync(
                start.toInstant(ZoneOffset.UTC),
                end.toInstant(ZoneOffset.UTC));
    }

    private void saveLogs(List<TrainingSessionLog> logs) {
        logRepository.saveAll(logs);
    }

    private static class ExerciseLog {
        int steps;
        int avgHR;
        LocalDateTime when;

        ExerciseLog(int steps, int avgHR, LocalDateTime when) {
            this.steps = steps;
            this.avgHR = avgHR;
            this.when = when;
        }
    }
}
