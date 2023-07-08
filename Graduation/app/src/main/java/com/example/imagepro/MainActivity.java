package com.example.imagepro;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.imagepro.BackGroundWork.SessionManager;
import com.example.imagepro.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends ParentDrawer {
    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.pre);
        drawerLayout =  findViewById(R.id.drawer);
        navigationView =  findViewById(R.id.nav);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Numbers");
        Drawer.setupDrawer(this,toolbar,drawerLayout,navigationView);

        replaceFrag(new NumbersFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.alpha) {
                replaceFrag(new AlphaFragment());
                Objects.requireNonNull(getSupportActionBar()).setTitle("Alpha");
            } else if (itemId == R.id.greet) {
                replaceFrag(new GreetingsFragment());
                Objects.requireNonNull(getSupportActionBar()).setTitle("Greeting");
            } else if (itemId == R.id.num) {
                replaceFrag(new NumbersFragment());
                Objects.requireNonNull(getSupportActionBar()).setTitle("Numbers");
            }
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.profile_menu_item) {
            IntentClass.goToActivity(this,Profile.class);
        }
        else if(menuItem.getItemId() == R.id.logout_menu_item){
            SessionManager sm=new SessionManager(this);
            sm.setLoggedIn(false);
            sm.clearSession();

            IntentClass.goToActivity(this,SigninActivity.class);
        }
        return true;
    }
    private void replaceFrag(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

}