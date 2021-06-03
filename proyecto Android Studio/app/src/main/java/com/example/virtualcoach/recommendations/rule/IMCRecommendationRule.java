package com.example.virtualcoach.recommendations.rule;

import com.example.virtualcoach.recommendations.ExerciseData;
import com.example.virtualcoach.recommendations.UserData;

public class IMCRecommendationRule implements RecommendationRule {
    @Override
    public boolean isRecommended(UserData userData, ExerciseData exerciseData) {

        if ((userData.isIMCVeryLow() || userData.isIMCVeryHigh()) && exerciseData.isIntense(userData))
            return false;

        return true;
    }
}
