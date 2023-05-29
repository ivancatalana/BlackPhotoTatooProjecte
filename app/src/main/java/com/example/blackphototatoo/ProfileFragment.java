package com.example.blackphototatoo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
    private NavController navController;
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Evita volver atras

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //  Handle the back button even
                // Aqui podemos configurar el comprotamiento del boton back
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };

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
        profile= view.findViewById(R.id.friendProfileImageView);
        navController = Navigation.findNavController(view);

        Glide.with(this)
                .load(getResources().getDrawable(R.drawable.profile__))
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
                        tab.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Acción para el ícono de la posición 0 (photo)
                                // Realiza la navegación o realiza alguna otra acción deseada
                                // Ejemplo de navegación:
                                navController.navigate(R.id.tabbed1AFragment);
                            }
                        });
                        break;
                    case 1:
                        tab.setIcon(R.drawable.notification);
                        tab.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Acción para el ícono de la posición 1 (notification)
                                // Realiza la navegación o realiza alguna otra acción deseada
                                // Ejemplo de navegación:
                                navController.navigate(R.id.tabbed1BFragment);
                            }
                        });
                        break;
                    case 2:
                        tab.setIcon(R.drawable.mail);
                        tab.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Acción para el ícono de la posición 2 (mail)
                                // Realiza la navegación o realiza alguna otra acción deseada
                                // Ejemplo de navegación:
                                navController.navigate(R.id.tabbed1CFragment);
                            }
                        });
                        break;
                }
            }
        }).attach();

        Button leftButton = view.findViewById(R.id.leftButtonProfile);
        Button rightButton = view.findViewById(R.id.rightButtonProfile);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition > 0) {
                    viewPager.setCurrentItem(currentPosition - 1);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition < viewPager.getAdapter().getItemCount() - 1) {
                    viewPager.setCurrentItem(currentPosition + 1);
                }
            }
        });

// Ocultar botón de la izquierda en el primer tab
        if (viewPager.getCurrentItem() == 0) {
            leftButton.setVisibility(View.GONE);
        }

// Ocultar botón de la derecha en el tercer tab
        if (viewPager.getCurrentItem() == viewPager.getAdapter().getItemCount() - 1) {
            rightButton.setVisibility(View.GONE);
        }

// Agregar un ViewPager.OnPageChangeListener para mostrar/ocultar los botones según la posición actual
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Mostrar u ocultar botón de la izquierda
                if (position == 0) {
                    leftButton.setVisibility(View.GONE);
                } else {
                    leftButton.setVisibility(View.VISIBLE);
                }

                // Mostrar u ocultar botón de la derecha
                if (position == viewPager.getAdapter().getItemCount() - 1) {
                    rightButton.setVisibility(View.GONE);
                } else {
                    rightButton.setVisibility(View.VISIBLE);
                }
            }
        });


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
        final ImageView photo = view.findViewById(R.id.friendProfileImageView);
        final TextView name = view.findViewById(R.id.nameProfile);
     //   final TextView email = view.findViewById(com.firebase.ui.auth.R.id.email);


        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    if (user.getPhotoUrl() != null) {
                        Glide.with(requireContext())
                                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                                .circleCrop()
                                .into(photo);


                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    //                    email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    } else {

                        Glide.with(getContext())
                                .load(R.drawable.profile)
                                .circleCrop()
                                .into(photo);


                        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//                        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                }
            }
        });
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }

}
