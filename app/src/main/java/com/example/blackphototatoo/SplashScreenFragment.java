package com.example.blackphototatoo;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashScreenFragment extends Fragment {
    NavController navController;   // <-----------------
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("...------------------------------------------------------------------" + mAuth.getCurrentUser().getDisplayName());

        return inflater.inflate(R.layout.fragment_blank, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        final Handler handler = new Handler(Looper.getMainLooper());
        System.out.println("...------------------------------------------------------------------" + mAuth.getCurrentUser().getDisplayName());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                accederAutomaticamente();
            }
        }, 2000);


    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

    }

    private void actualizarUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            navController.navigate(R.id.homeFragment);
        }
        else navController.navigate(R.id.loginFragment);
    }

    private void accederAutomaticamente() {
        final Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                FirebaseUser user = mAuth.getCurrentUser();
                actualizarUI(mAuth.getCurrentUser());
            }
        }, 2000);
    }
}