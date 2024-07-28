package com.demo.furnitureapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    private static final String SHARED_PREFERENCES_NAME = "PreferencesUtil.Furniture.App";
    private static final String STAY_LOGIN = "STAY_LOGIN";
    private static final String LOGIN_AS_ADMIN = "LOGIN_AS_ADMIN";
    private static final String LOGGED_EMAIL = "LOGGED_EMAIL";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String LAST_NAME = "LAST_NAME";
    private static final String PROFILE_IMAGE = "PROFILE_IMAGE";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setStayLogin(Context context, boolean stayLogged) {
        getSharedPreferences(context).edit().putBoolean(STAY_LOGIN, stayLogged).apply();
    }

    public static boolean getStayLogin(Context context) {
        return getSharedPreferences(context).getBoolean(STAY_LOGIN, false);
    }

    public static void setLoginAsAdmin(Context context, boolean loginAsAdmin) {
        getSharedPreferences(context).edit().putBoolean(LOGIN_AS_ADMIN, loginAsAdmin).apply();
    }

    public static boolean getLoginAsAdmin(Context context) {
        return getSharedPreferences(context).getBoolean(LOGIN_AS_ADMIN, false);
    }

    public static void setLoggedEmail(Context context, String loggedEmail) {
        getSharedPreferences(context).edit().putString(LOGGED_EMAIL, loggedEmail).apply();
    }

    public static String getLoggedEmail(Context context) {
        return getSharedPreferences(context).getString(LOGGED_EMAIL, "");
    }

    public static void setFirstName(Context context, String firstName) {
        getSharedPreferences(context).edit().putString(FIRST_NAME, firstName).apply();
    }

    public static String getFirstName(Context context) {
        return getSharedPreferences(context).getString(FIRST_NAME, "");
    }

    public static void setLastName(Context context, String lastName) {
        getSharedPreferences(context).edit().putString(LAST_NAME, lastName).apply();
    }

    public static String getLastName(Context context) {
        return getSharedPreferences(context).getString(LAST_NAME, "");
    }

    public static void setProfileImage(Context context, String profileImage) {
        getSharedPreferences(context).edit().putString(PROFILE_IMAGE, profileImage).apply();
    }

    public static String getProfileImage(Context context) {
        return getSharedPreferences(context).getString(PROFILE_IMAGE, "");
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}
