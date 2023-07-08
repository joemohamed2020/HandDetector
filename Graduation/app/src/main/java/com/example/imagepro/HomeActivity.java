package com.example.imagepro;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.imagepro.BackGroundWork.SessionManager;
import com.example.imagepro.Detection.Combine;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class HomeActivity extends ParentDrawer  {
    LinearLayout catalogLayout,liveLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView userNameTextView,emailTextView;
    private boolean isBackPressedOnce =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionManager sessionManager =new SessionManager(this);
        if (!sessionManager.isLoggedIn()){
            IntentClass.goToActivity(this, SignupActivity.class);
        }
        Profile profile =new Profile();
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
        emailTextView.setText(sessionManager.getLoggedInEmail());
        userNameTextView.setText(sessionManager.getLoggedInUsername());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        Drawer.setupDrawer(this,toolbar,drawerLayout,navigationView);
        catalogLayout.setOnClickListener(view -> IntentClass.goToActivity(this,MainActivity.class));
        liveLayout.setOnClickListener(view -> IntentClass.goToActivity(HomeActivity.this, Combine.class));
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
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (isBackPressedOnce){
            this.finish();
        }
        else {
            Toast.makeText(this,"Back Again To Exit",Toast.LENGTH_SHORT).show();
            isBackPressedOnce = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isBackPressedOnce = false;
                }
            },2000);
        }
    }
}