package swiftshop.glosoftgroup.com.shoppay;

import android.content.Context;
import android.content.SharedPreferences;

import swiftshop.glosoftgroup.com.shoppay.entities.Shop;
import swiftshop.glosoftgroup.com.shoppay.entities.User;
import swiftshop.glosoftgroup.com.shoppay.utils.Utils;
import timber.log.Timber;

/**
 * Created by admin on 12/15/2016.
 */

public class SettingsMy {
    public static final String PREF_ACTUAL_SHOP = "pref_actual_shop";
    public static final String PREF_ACTIVE_USER = "pref_active_user";
    public static final String PREF_USER_EMAIL = "pref_user_email";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    private static final String TAG = SettingsMy.class.getSimpleName();
    private static Shop actualShop;
    private static User activeUser;
    private static SharedPreferences sharedPref;

    private SettingsMy() {}

    /**
     * Get active user info.
     *
     * @return user or null if nobody logged in.
     */
    public static User getActiveUser() {
        if (activeUser != null) {
            Timber.d("%s - Returned active user", TAG);
            return activeUser;
        } else {
            SharedPreferences prefs = getSettings();
            String json = prefs.getString(PREF_ACTIVE_USER, "");
            if (json.isEmpty() || "null".equals(json)) {
                Timber.d("%s - Returned null", TAG);
                return null;
            } else {
                activeUser = Utils.getGsonParser().fromJson(json, User.class);
                Timber.d("%s - Returned active user from memory: %s", TAG, activeUser.toString());
                return activeUser;
            }
        }
    }

    /**
     * Set active user.
     *
     * @param user active user or null for disable user.
     */
    public static void setActiveUser(User user) {
        if (user != null)
            Timber.d("%s - Set active user with name: %s", TAG, user.toString());
        else
            Timber.d("%s - Deleting active user", TAG);
        SettingsMy.activeUser = user;

        String json = Utils.getGsonParser().toJson(SettingsMy.activeUser);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTIVE_USER, json);
        editor.apply();
    }

    /**
     * Get user email. Used for login purpose.
     *
     * @return email of last logged user.
     */
    public static String getUserEmailHint() {
        SharedPreferences prefs = getSettings();
        String userEmail = prefs.getString(PREF_USER_EMAIL, "");
        Timber.d("%s - Obtained user email: %s", TAG, userEmail);
        return userEmail;
    }

    /**
     * Set user email to preferences.
     * Used for login purpose.
     *
     * @param userEmail email of last logged user.
     */
    public static void setUserEmailHint(String userEmail) {
        Timber.d("%s - Set user email: %s", TAG, userEmail);
        putParam(PREF_USER_EMAIL, userEmail);
    }

    /**
     * Get indicator, that GCM token was sent to third party server.
     *
     * @return true if successfully received by third party server. False otherwise.
     */
    public static Boolean getTokenSentToServer() {
        SharedPreferences prefs = getSettings();
        boolean tokenSent = prefs.getBoolean(SENT_TOKEN_TO_SERVER, false);
        Timber.d("%s - Obtained token sent to server: %s", TAG, tokenSent);
        return tokenSent;
    }

    /**
     * Set GCM token sent to third party server indicator.
     *
     * @param tokenSent true if successfully received by server.
     */
    public static void setTokenSentToServer(boolean tokenSent) {
        putParam(SENT_TOKEN_TO_SERVER, tokenSent);
    }

    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    public static SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = MyApplication.getInstance().getSharedPreferences(MyApplication.PACKAGE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPref;
    }

    private static boolean putParam(String key, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private static boolean putParam(String key, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }
}
