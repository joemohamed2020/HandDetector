package com.example.imagepro;

import android.app.Activity;
import android.content.Intent;

public class IntentClass {
    public static void goToActivity(Activity srcActivity, Class<?> destActivity) {
        Intent intent = new Intent(srcActivity, destActivity);
        srcActivity.startActivity(intent);
    }
}
