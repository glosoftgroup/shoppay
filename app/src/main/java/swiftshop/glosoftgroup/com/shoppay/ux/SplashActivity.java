package swiftshop.glosoftgroup.com.shoppay.ux;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import swiftshop.glosoftgroup.com.shoppay.CONST;
import swiftshop.glosoftgroup.com.shoppay.MyApplication;
import swiftshop.glosoftgroup.com.shoppay.R;
import swiftshop.glosoftgroup.com.shoppay.decorations.EspressoIdlingResource;
import swiftshop.glosoftgroup.com.shoppay.utils.Utils;
import timber.log.Timber;

/**
 * Created by admin on 12/15/2016.
 */

public class SplashActivity extends AppCompatActivity{
    public static final String REFERRER = "referrer";
    private static final String TAG = SplashActivity.class.getSimpleName();

    private Activity activity;
    private ProgressDialog progressDialog;

    /**
     * Indicates if layout has been already created.
     */
    private boolean layoutCreated = false;

    /**
     * Indicates that window has been already detached.
     */
    private boolean windowDetached = false;

    /**
     * Indicates if reconnection has been tried if internet is discovered off.
     */
    private boolean connectionRetry = false;

    // Possible layouts
    private View layoutIntroScreen;
    private View layoutContent;
    private View layoutContentNoConnection;
    private View layoutContentSelectShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        activity = this;

        // init loading dialog
        progressDialog = Utils.generateProgressDialog(this, false);

        init();
    }

    private void init() {
        // Check if data connected.
        if (!MyApplication.getInstance().isDataConnected()) {
            progressDialog.hide();
            Timber.d("No network connection.");

            if(connectionRetry) {
                //just go to go dashboard anyway
                startMainActivity(null);
            }else {
                initSplashLayout();
            }

            // Skip intro screen.
            layoutContent.setVisibility(View.VISIBLE);
            layoutIntroScreen.setVisibility(View.GONE);

            // Show retry button.
            layoutContentNoConnection.setVisibility(View.VISIBLE);
            layoutContentSelectShop.setVisibility(View.GONE);
        } else {
            progressDialog.hide();

            Timber.d("Nothing special.");
            startMainActivity(null);
        }

    }

    private void startMainActivity(Bundle bundle) {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        if (bundle != null) {
            Timber.d("Pass bundle to main activity");
            mainIntent.putExtras(bundle);
        }
        startActivity(mainIntent);
        finish();
    }

    private void initSplashLayout() {
        connectionRetry = true;
        Timber.d("reconnection retry set true...");
        if (!layoutCreated) {
            setContentView(R.layout.activity_splash);

            layoutContent = findViewById(R.id.splash_content);
            layoutIntroScreen = findViewById(R.id.splash_intro_screen);
            layoutContentNoConnection = findViewById(R.id.splash_content_no_connection);
            layoutContentSelectShop = findViewById(R.id.splash_content_select_shop);
            Button reRunButton = (Button) findViewById(R.id.splash_re_run_btn);
            if (reRunButton != null) {
                reRunButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        }, 600);
                    }
                });
            } else {
                Timber.e(new RuntimeException(), "ReRunButton didn't found");
            }
            layoutCreated = true;
        } else {
            Timber.d("%s screen is already created.", this.getClass().getSimpleName());
        }

    }

    /**
     * Hide intro screen and display content layout with animation.
     */
    private void animateContentVisible() {
        if (layoutIntroScreen != null && layoutContent != null && layoutIntroScreen.getVisibility() == View.VISIBLE) {
            EspressoIdlingResource.increment();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (windowDetached) {
                                if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
                            } else {
//                            // If lollipop use reveal animation. On older phones use fade animation.
                                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    Timber.d("Circular animation.");
                                    // get the center for the animation circle
                                    final int cx = (layoutContent.getLeft() + layoutContent.getRight()) / 2;
                                    final int cy = (layoutContent.getTop() + layoutContent.getBottom()) / 2;

                                    // get the final radius for the animation circle
                                    int dx = Math.max(cx, layoutContent.getWidth() - cx);
                                    int dy = Math.max(cy, layoutContent.getHeight() - cy);
                                    float finalRadius = (float) Math.hypot(dx, dy);

                                    Animator animator = ViewAnimationUtils.createCircularReveal(layoutContent, cx, cy, 0, finalRadius);
                                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                    animator.setDuration(1250);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    animator.start();
                                } else {
                                    Timber.d("Alpha animation.");
                                    layoutContent.setAlpha(0f);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    layoutContent.animate()
                                            .alpha(1f)
                                            .setDuration(1000)
                                            .setListener(null);
                                }
                            }
                            EspressoIdlingResource.decrement();
                        }
                    }, 330);
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        if (progressDialog != null) progressDialog.cancel();
        if (layoutIntroScreen != null) layoutIntroScreen.setVisibility(View.GONE);
        if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
        MyApplication.getInstance().cancelPendingRequests(CONST.SPLASH_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        windowDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        windowDetached = true;
        super.onDetachedFromWindow();
    }
}
