package com.example.virtualcoach.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Preconditions;
import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;

public class TestFragmentLauncher {
    private static void launchFragmentInHiltContainer(
            Class<? extends Fragment> fragmentClass,
            ActivityScenario<? extends AppCompatActivity> activityScenario) {

        Bundle fragmentArgs = null;
//        @StyleRes Integer themeResId = R.style.FragmentScenarioEmptyFragmentActivityTheme;
//        Intent startActivityIntent = Intent.makeMainActivity(
//                new ComponentName(
//                        ApplicationProvider.getApplicationContext(),
//                        MainActivity.class
//                )
//        ).putExtra(FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId);

        activityScenario.onActivity(activity -> {
            Fragment fragment = activity.getSupportFragmentManager().getFragmentFactory().instantiate(
                    Preconditions.checkNotNull(fragmentClass.getClassLoader()),

                    fragmentClass.getName());

            fragment.setArguments(fragmentArgs);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, "")
                    .commitNow();

        });
    }
}
