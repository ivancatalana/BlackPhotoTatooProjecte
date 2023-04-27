package com.example.blackphototatoo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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


        // Obtener una referencia al DrawerLayout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // Obtener una referencia al NavigationView
        NavigationView navView = findViewById(R.id.nav_view);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.bottom2Fragment) {
                    // Mostrar Drawer Menu
                    drawerLayout.openDrawer(GravityCompat.END);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                else {
                   // Ocultar el menú de navegación
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                }

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
                        || destination.getId() == R.id.tatooStoreFragment
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
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyRecyclerViewFragment(), "Tab 1");
        adapter.addFragment(new MyRecyclerViewFragment(), "Tab 2");
        adapter.addFragment(new MyRecyclerViewFragment(), "Tab 3");
      //  viewPager.setAdapter(adapter);
       // tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        // No hacemos nada para evitar que el botón de "Atrás" funcione
    }
}