package com.example.virtualcoach.recommendations.rule;

import com.example.virtualcoach.recommendations.ExerciseData;
import com.example.virtualcoach.recommendations.UserData;

public interface RecommendationRule {
    boolean isRecommended(UserData userData, ExerciseData exerciseData);
}
