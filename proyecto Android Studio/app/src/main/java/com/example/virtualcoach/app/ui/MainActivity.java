package com.example.virtualcoach.app.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.virtualcoach.R;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.google.android.material.navigation.NavigationView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);
    private NavController navController;

    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.fragment_app_drawer_inner);

        final boolean isloggedIn = userRepository.isLoggedIn();

        if (!isloggedIn) {
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            navGraph.setStartDestination(R.id.loginFragment);
            navController.setGraph(navGraph);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(isloggedIn ?
                R.string.destination_training_progress_title :
                R.string.destination_login_title);

        final DrawerLayout drawerLayout = findViewById(R.id.activity_main_drawer);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.loginFragment, R.id.trainingProgressFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        setSupportActionBar(toolbar);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        NavigationView navigationView = findViewById(R.id.activity_main_drawer_nav);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                toolbar.setNavigationIcon(null);
            } else if (destination.getId() == R.id.registerFragment) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                Objects.requireNonNull(getSupportActionBar()).show();
            }
        });


        LOG.debug("Initialized");
    }
}