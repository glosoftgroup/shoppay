package swiftshop.glosoftgroup.com.shoppay;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import swiftshop.glosoftgroup.com.shoppay.api.OkHttpStack;
import swiftshop.glosoftgroup.com.shoppay.decorations.EspressoIdlingResource;
import timber.log.Timber;

/**
 * Created by admin on 12/15/2016.
 */

public class MyApplication extends Application{
    public static final String PACKAGE_NAME = MyApplication.class.getPackage().getName();

    private static final String TAG = MyApplication.class.getSimpleName();


    public static String APP_VERSION = "0.0.0";
    public static String ANDROID_ID = "0000000000000000";

    private static MyApplication mInstance;

    private RequestQueue mRequestQueue;


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {

        }

        try {
            ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (ANDROID_ID == null || ANDROID_ID.isEmpty()) {
                ANDROID_ID = "0000000000000000";
            }
        } catch (Exception e) {
            ANDROID_ID = "0000000000000000";
        }
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            APP_VERSION = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            Timber.e(e, "App versionName not found. This should never happen.");
        }
    }

    /**
     * Method check, if internet is available.
     *
     * @return true if internet is available. Else otherwise.
     */
    public boolean isDataConnected() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isWiFiConnection() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Volley processing
     *
     * */

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());
        }
        return mRequestQueue;
    }

    @VisibleForTesting
    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
