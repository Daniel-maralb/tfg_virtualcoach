package com.example.virtualcoach.recommendations.rule;

import com.example.virtualcoach.recommendations.ExerciseData;
import com.example.virtualcoach.recommendations.UserData;

public class IntensityRecommendationRule implements RecommendationRule {
    @Override
    public boolean isRecommended(UserData userData, ExerciseData exerciseData) {
        if (exerciseData.isNotIntenseEnough(userData) || exerciseData.isTooIntense(userData))
            return false;
        return true;
    }
}

