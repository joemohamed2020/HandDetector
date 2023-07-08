package com.example.imagepro;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class Drawer {

    public static void setupDrawer(ParentDrawer activity, Toolbar toolbar, DrawerLayout drawerLayout, NavigationView navigationView) {
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open, R.string.close);
        navigationView.setNavigationItemSelectedListener(activity);
        navigationView.setNavigationItemSelectedListener(activity);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

}

