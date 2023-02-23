package com.example.blackphototatoo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.blackphototatoo.databinding.ActivityMainBinding;
import com.example.blackphototatoo.databinding.FragmentBottom3Binding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());




        navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {

                if (destination.getId() == R.id.fullscreenStartFragment) {
                    // Hide status bar
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                else {
                    // Show status bar
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
                if (destination.getId() == R.id.bottom1Fragment
                        || destination.getId() == R.id.bottom2Fragment
                        || destination.getId() == R.id.bottom3Fragment
                        || destination.getId() == R.id.discoverFragment
                        || destination.getId() == R.id.uploadPhotoFragment)  {
                    binding.bottomNavView.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomNavView.setVisibility(View.GONE);
                }
            }
        });
        binding.bottomNavView.findViewById(R.id.bottom3Fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });
    }

}