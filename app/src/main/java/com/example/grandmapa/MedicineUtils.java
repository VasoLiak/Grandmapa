package com.example.grandmapa;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicineUtils {
    private static final String PREFS_NAME = "medicines_prefs";
    private static final String MEDICINES_KEY_PREFIX = "medicines_";

    public static void saveMedicines(Context context, String date, List<Medicine> medicines) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicines);
        editor.putString(MEDICINES_KEY_PREFIX + date, json);
        editor.apply();
    }

    public static List<Medicine> loadMedicines(Context context, String date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(MEDICINES_KEY_PREFIX + date, null);
        Type type = new TypeToken<List<Medicine>>() {}.getType();
        List<Medicine> medicines = gson.fromJson(json, type);
        return medicines != null ? medicines : new ArrayList<>();
    }
}
