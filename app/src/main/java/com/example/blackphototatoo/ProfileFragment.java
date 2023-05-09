package com.example.blackphototatoo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.blackphototatoo.databinding.FragmentProfileBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageView profile;
    private Button editProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;
        editProfile=binding.button5;
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#D4AF37"));

        // Cambiar tamaño de letra de las Tabs
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profile= view.findViewById(R.id.imagen_perfil);

        Glide.with(this)
                .load(getResources().getDrawable(R.drawable.profile))
                .circleCrop()
                .placeholder(R.drawable.borde)
                .into(profile);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: default:
                        return new Tabbed1AFragment();
                    case 1:
                        return new Tabbed1BFragment();
                    case 2:
                        return new Tabbed1CFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: default:
                        tab.setIcon(R.drawable.photo);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.notification);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.mail);
                        break;
                }
            /*    // Creamos un TextView personalizado para el título de la Tab
                TextView tabTitle = new TextView(getContext());
                tabTitle.setText(tab.getText());
                tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17); // Cambiamos el tamaño de letra aquí
                tabTitle.setTypeface(tabTitle.getTypeface(), Typeface.BOLD); // Establecemos la letra en negrita
                //tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.semi_transparent_gold)); // Establecemos el color de letra
                tab.setCustomView(tabTitle); // Establecemos el TextView personalizado como vista para la Tab
                0
             */
            }
        }).attach();

            NavController navController = Navigation.findNavController(view);

            // navegar a otro fragmento
            view.findViewById(R.id.button5).setOnClickListener(v -> {
                navController.navigate(R.id.editProfileFragment);
            });
            //----------------------------------------------------------------------------------------------------------//----------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------//----------------------------------------------------------------------------------------------------------
            //--------------------------------------------------Imagen y nombre de usuario--------------------------------------------------------//----------------------------------------------------------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------//----------------------------------------------------------------------------------------------------------

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header = navigationView.getHeaderView(0);
        final ImageView photo = view.findViewById(R.id.imagen_perfil);
        final TextView name = view.findViewById(R.id.nameProfile);
     //   final TextView email = view.findViewById(com.firebase.ui.auth.R.id.email);


        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    if (user.getPhotoUrl() != null) {
                        Glide.with(getContext())
                                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                                .circleCrop()
                                .into(photo);


                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    //                    email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    } else {

                        Glide.with(getContext())
                                .load(R.drawable.profile_)
                                .circleCrop()
                                .into(photo);


                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//                        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
