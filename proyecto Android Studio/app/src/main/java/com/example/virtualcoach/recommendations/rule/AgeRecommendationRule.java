package com.example.virtualcoach.recommendations.rule;

import com.example.virtualcoach.recommendations.ExerciseData;
import com.example.virtualcoach.recommendations.UserData;

public class AgeRecommendationRule implements RecommendationRule {
    @Override
    public boolean isRecommended(UserData userData, ExerciseData exerciseData) {

        if (userData.isOld() && exerciseData.isIntense(userData))
            return false;

        return true;
    }
}
