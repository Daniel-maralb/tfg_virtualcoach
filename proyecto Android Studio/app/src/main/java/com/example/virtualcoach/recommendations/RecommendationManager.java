package com.example.virtualcoach.recommendations;

import com.example.virtualcoach.app.util.NullUtils;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.recommendations.rule.AgeRecommendationRule;
import com.example.virtualcoach.recommendations.rule.IMCRecommendationRule;
import com.example.virtualcoach.recommendations.rule.IntensityRecommendationRule;
import com.example.virtualcoach.recommendations.rule.RecommendationRule;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class RecommendationManager {
    public static final int NUM_RECOMMENDATIONS = 3;
    private final List<RecommendationRule> blacklist;
    private final List<RecommendationRule> whitelist;

    private final UserData userData;
    private final boolean incompleteUser;

    @Inject
    public RecommendationManager(UserRepository userRepository) {
        DisplayUser currentUser = userRepository.getCurrentUser();
        if (NullUtils.anyNull(currentUser.age, currentUser.height, currentUser.weight)) {
            incompleteUser = true;
            userData = null;
            blacklist = Collections.emptyList();
            whitelist = Collections.emptyList();
        } else {
            incompleteUser = false;

            userData = new UserData(currentUser);
            blacklist = List.of(
                    new IntensityRecommendationRule(),
                    new IMCRecommendationRule(),
                    new AgeRecommendationRule()
            );
            whitelist = Collections.emptyList();
        }
    }

    public List<Exercise> getRecommendations(List<Exercise> allExercises) {
        if (incompleteUser)
            return Collections.emptyList();

        List<Exercise> result = new ArrayList<>(allExercises);

        ArrayList<ExerciseData> exerciseDataList = new ArrayList<>(allExercises.size());

        for (Exercise e : allExercises) {
            exerciseDataList.add(new ExerciseData(e));
        }

        for (RecommendationRule rule : blacklist) {
            for (ExerciseData ed : exerciseDataList) {
                if (!rule.isRecommended(userData, ed))
                    result.remove(ed.exercise);
            }
        }

        for (RecommendationRule rule : blacklist) {
            for (ExerciseData ed : exerciseDataList) {
                if (rule.isRecommended(userData, ed))
                    result.add(ed.exercise);
            }
        }

        Collections.shuffle(result);

        if (result.size() > NUM_RECOMMENDATIONS)
            result = result.subList(0, NUM_RECOMMENDATIONS);

        return result;
    }
}
