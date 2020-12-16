package com.example.remotemonitoringapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SessionHandler {

    private static final String PREF_EVENTS_NAME = "Pop-InEventsSession";
    private static final String KEY_EVENTS_EXPIRES = "EventsExpires";
    private static final String KEY_EVENTS_LOGGED = "EventsLoggedStat";
    private static final String KEY_EVENTS_USER_ID = "EventUserId";

    private static final String PREF_NAME = "Pop-InSession";
    private static final String KEY_USER_ID = "UserId";
    private static final String KEY_EXPIRES = "Expires";
    private static final String KEY_LOGGED = "LoggedStat";
    private static final String KEY_EMPTY = "";
    private Context context;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    private SharedPreferences.Editor mEventsEditor;
    private SharedPreferences mEventsPreferences;

    public SessionHandler(Context mContext)
    {
        this.context = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();

        mEventsPreferences = mContext.getSharedPreferences(PREF_EVENTS_NAME,Context.MODE_PRIVATE);
        this.mEventsEditor = mEventsPreferences.edit();
    }

    //login user and save userid as session
    public void login(String UserId)
    {
        mEditor.putString(KEY_USER_ID,UserId);
        mEditor.putBoolean(KEY_LOGGED,true);

        //set user session for 1 minute
        Date date = new Date();
        long millis = date.getTime()+(30*24*60*60*1000);
        mEditor.putLong(KEY_EXPIRES,millis);
        mEditor.commit();
    }

    public void events_login(String UserId)
    {
        mEventsEditor.putString(KEY_EVENTS_USER_ID,UserId);
        mEventsEditor.putBoolean(KEY_EVENTS_LOGGED,true);

        //set user session for 1 minute
        Date date = new Date();
        long millis = date.getTime()+(30*24*60*60*1000);
        mEventsEditor.putLong(KEY_EVENTS_EXPIRES,millis);
        mEventsEditor.commit();
    }

    //check whether the user is logged in
    public boolean isLoggedIn()
    {

        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES,0);

        //if shared preferences does not have a value, then user is not logged in
        if(millis == 0) {
            return false;
        }

        Date expiryDate = new Date(millis);

        if(currentDate.before(expiryDate)) {
            return true;
        } else {
            return false;
        }

    }

    //fetch and return the user_id
    public User getUserDetails()
    {
        //check if user is logged in first
        if(!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setUserID(mPreferences.getString(KEY_USER_ID,KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES,0)));

        return user;
    }

    //logout user by clearing session
    public void logout()
    {
        mEditor.clear();
        mEditor.commit();
    }

    public void logout_events()
    {
        mEventsEditor.clear();
        mEventsEditor.commit();
    }

}

