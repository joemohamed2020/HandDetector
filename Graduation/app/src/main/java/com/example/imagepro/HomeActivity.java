package com.example.imagepro;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class HomeActivity extends ParentDrawer  {
    LinearLayout catalogLayout,liveLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView userNameTextView,emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.home_tool_bar);
        drawerLayout =  findViewById(R.id.drawer);
        navigationView =  findViewById(R.id.nav);
        catalogLayout = findViewById(R.id.catalog_layout);
        liveLayout = findViewById(R.id.liveLayout);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.user_name_text_view);
        emailTextView = headerView.findViewById(R.id.email_text_view);
        emailTextView.setText("email");
        userNameTextView.setText("UserName");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        Drawer.setupDrawer(this,toolbar,drawerLayout,navigationView);
        catalogLayout.setOnClickListener(view -> IntentClass.goToActivity(this,MainActivity.class));
        liveLayout.setOnClickListener(view -> IntentClass.goToActivity(this,Combine.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.profile_menu_item) {
            IntentClass.goToActivity(this,Profile.class);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}