package ojassadmin.nitjsr.in.ojassadmin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Abhishek on 26-Jan-18.
 */

public class SharedPrefManager {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREF = "SharedPref";
    private static final String IS_FIRST_OPEN = "isFirstOpen";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String IS_REGISTERED = "isRegistered";
    private static final String ACCESS_LEVEL = "accessLevel";
    private static final String IS_PHONE_VERIFIED = "isPhoneVerified";



    public SharedPrefManager(Context context){
        sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public boolean isFirstOpen(){
        return sharedPref.getBoolean(IS_FIRST_OPEN, true);
    }

    public boolean isLoggedIn() {
        return sharedPref.getBoolean(IS_LOGGED_IN, false);
    }

    public boolean isRegistered(){
        return sharedPref.getBoolean(IS_REGISTERED, false);
    }



    public boolean isPhoneVerified() { return sharedPref.getBoolean(IS_PHONE_VERIFIED, false); }


    public void setIsFirstOpen(boolean isFirstOpen){
        editor.putBoolean(IS_FIRST_OPEN, isFirstOpen).apply();
    }


    public void setIsLoggedIn(boolean isLoggedIn){
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn).apply();
    }

    public void setIsRegistered(boolean isRegistered){
        editor.putBoolean(IS_REGISTERED, isRegistered).apply();
    }

    public void setIsPhoneVerified(boolean isPhoneVerified){
        editor.putBoolean(IS_PHONE_VERIFIED, isPhoneVerified).apply();
    }

    public void setAccessLevel(int accessLevel){
        editor.putInt(ACCESS_LEVEL, accessLevel).apply();
    }

    public int getAccessLevel(){
        return sharedPref.getInt(ACCESS_LEVEL, 3);
    }

}