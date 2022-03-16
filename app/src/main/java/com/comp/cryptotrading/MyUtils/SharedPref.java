package com.comp.cryptotrading.MyUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

public class SharedPref {
    public static final void save(Context context, String key, double val){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, (float) val);
        editor.commit();
    }

    public static final void save(Context context, String key, boolean val){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public static final void save(Context context, String key, String val){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static final Double getValue(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        DecimalFormat df = new DecimalFormat("0.000000");
        double val = Double.valueOf(pref.getFloat(key,-1));
        Double formatedVal = Double.valueOf(df.format(val));
        return formatedVal;
    }

    public static final String getValueStr(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String val = pref.getString(key, "");
        return val;
    }
}
