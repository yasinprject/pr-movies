package pr.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String URL_1 = "https://prmovies.fitness/";
    private static final String URL_2 = "https://watchomovies.design/";
    private static final String URL_3 = "https://xhamster.com/"; 
    private static final String URL_4 = "https://www.xnxx.com/"; 
    private String currentUrl = URL_1;

    private WebView myWebView;
    private ProgressBar topProgress;
    private FrameLayout fullscreenContainer;
    private View splashLayout;
    private LinearLayout lockScreenLayout;
    private EditText etPassword;
    private Button btnUnlock;

    private LinearLayout tab1, tab2, tab3, tab4;
    private ImageView iconTab1, iconTab2, iconTab3, iconTab4;
    private TextView textTab1, textTab2, textTab3, textTab4;

    private boolean isAppUnlocked = false; 
    private boolean isFullscreen = false;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private GestureDetector gestureDetector;
    private AudioManager audioManager;
    private int maxVolume;
    private LinearLayout swipeIndicatorLayout;
    private ImageView swipeIndicatorIcon;
    private TextView swipeIndicatorText;
    private int accumulatedSeek = 0; 
    private boolean isSeeking = false, isVolume = false, isBrightness = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        myWebView = findViewById(R.id.myWebView);
        topProgress = findViewById(R.id.topProgress);
        fullscreenContainer = findViewById(R.id.fullscreenContainer);
        splashLayout = findViewById(R.id.splashLayout);
        lockScreenLayout = findViewById(R.id.lockScreenLayout);
        etPassword = findViewById(R.id.etPassword);
        btnUnlock = findViewById(R.id.btnUnlock);

        swipeIndicatorLayout = findViewById(R.id.swipeIndicatorLayout);
        swipeIndicatorIcon = findViewById(R.id.swipeIndicatorIcon);
        swipeIndicatorText = findViewById(R.id.swipeIndicatorText);

        tab1 = findViewById(R.id.tab1); tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3); tab4 = findViewById(R.id.tab4);
        iconTab1 = findViewById(R.id.iconTab1); iconTab2 = findViewById(R.id.iconTab2);
        iconTab3 = findViewById(R.id.iconTab3); iconTab4 = findViewById(R.id.iconTab4);
        textTab1 = findViewById(R.id.textTab1); textTab2 = findViewById(R.id.textTab2);
        textTab3 = findViewById(R.id.textTab3); textTab4 = findViewById(R.id.textTab4);

        updateTabUI(1);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        splashLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLayout.setVisibility(View.GONE);
                lockScreenLayout.setVisibility(View.VISIBLE); 
            }
        }, 1500);

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().trim().equals("2026")) {
                    lockScreenLayout.setVisibility(View.GONE);
                    isAppUnlocked = true;
                    setupWebView();
                    myWebView.setVisibility(View.VISIBLE);
                    myWebView.loadUrl(currentUrl);
                } else {
                    Toast.makeText(MainActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View.OnClickListener tabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAppUnlocked) return;
                int id = v.getId();
                if (id == R.id.tab1 && !currentUrl.equals(URL_1)) { currentUrl = URL_1; updateTabUI(1); myWebView.loadUrl(currentUrl); } 
                else if (id == R.id.tab2 && !currentUrl.equals(URL_2)) { currentUrl = URL_2; updateTabUI(2); myWebView.loadUrl(currentUrl); } 
                else if (id == R.id.tab3 && !currentUrl.equals(URL_3)) { currentUrl = URL_3; updateTabUI(3); myWebView.loadUrl(currentUrl); } 
                else if (id == R.id.tab4 && !currentUrl.equals(URL_4)) { currentUrl = URL_4; updateTabUI(4); myWebView.loadUrl(currentUrl); }
            }
        };

        tab1.setOnClickListener(tabClickListener); tab2.setOnClickListener(tabClickListener);
        tab3.setOnClickListener(tabClickListener); tab4.setOnClickListener(tabClickListener);

        setupGestureControl();
    }

    private void updateTabUI(int selectedTab) {
        int grayColor = 0xFF888888; int redColor = 0xFFE50914;
        iconTab1.setColorFilter(grayColor, PorterDuff.Mode.SRC_IN); textTab1.setTextColor(grayColor);
        iconTab2.setColorFilter(grayColor, PorterDuff.Mode.SRC_IN); textTab2.setTextColor(grayColor);
        iconTab3.setColorFilter(grayColor, PorterDuff.Mode.SRC_IN); textTab3.setTextColor(grayColor);
        iconTab4.setColorFilter(grayColor, PorterDuff.Mode.SRC_IN); textTab4.setTextColor(grayColor);

        if (selectedTab == 1) { iconTab1.setColorFilter(redColor, PorterDuff.Mode.SRC_IN); textTab1.setTextColor(redColor); }
        else if (selectedTab == 2) { iconTab2.setColorFilter(redColor, PorterDuff.Mode.SRC_IN); textTab2.setTextColor(redColor); }
        else if (selectedTab == 3) { iconTab3.setColorFilter(redColor, PorterDuff.Mode.SRC_IN); textTab3.setTextColor(redColor); }
        else if (selectedTab == 4) { iconTab4.setColorFilter(redColor, PorterDuff.Mode.SRC_IN); textTab4.setTextColor(redColor); }
    }

    private void setupWebView() {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 Chrome/118.0.0.0 Safari/537.36");
        CookieManager.getInstance().setAcceptCookie(true);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                topProgress.setProgress(0); topProgress.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) { topProgress.setVisibility(View.GONE); }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) { topProgress.setVisibility(View.VISIBLE); topProgress.setProgress(newProgress); } 
                else topProgress.setVisibility(View.GONE);
            }
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) { callback.onCustomViewHidden(); return; }
                customView = view; customViewCallback = callback;
                
                fullscreenContainer.addView(view, 0); 
                fullscreenContainer.setVisibility(View.VISIBLE);
                myWebView.setVisibility(View.GONE);
                isFullscreen = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                fullscreenContainer.removeView(customView);
                fullscreenContainer.setVisibility(View.GONE);
                customView = null;
                myWebView.setVisibility(View.VISIBLE);
                isFullscreen = false;
                if (customViewCallback != null) customViewCallback.onCustomViewHidden();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isFullscreen && gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isSeeking && accumulatedSeek != 0) {
                    String js = "var vids = document.getElementsByTagName('video'); if(vids.length > 0) { vids[0].currentTime += " + accumulatedSeek + "; }";
                    myWebView.evaluateJavascript(js, null);
                    accumulatedSeek = 0;
                }
                isSeeking = isVolume = isBrightness = false;
                hideSwipeIndicator();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void setupGestureControl() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private int startVolume; private float startBrightness;
            @Override
            public boolean onDown(MotionEvent e) {
                startVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                startBrightness = lp.screenBrightness;
                if (startBrightness < 0) startBrightness = 0.5f;
                return true; 
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float deltaX = e2.getX() - e1.getX(); 
                float deltaY = e1.getY() - e2.getY(); 
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                if (!isSeeking && !isVolume && !isBrightness) {
                    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 40) isSeeking = true;
                    else if (Math.abs(deltaY) > 40) {
                        if (e1.getX() < screenWidth / 2f) isBrightness = true;
                        else isVolume = true;
                    }
                }

                if (isSeeking) {
                    accumulatedSeek = (int) (deltaX / 10); 
                    String sign = accumulatedSeek > 0 ? "+" : "";
                    showSwipeIndicator(android.R.drawable.ic_media_ff, sign + accumulatedSeek + "s");
                } else if (isVolume) {
                    int volChange = (int) (deltaY / (screenHeight / maxVolume));
                    int newVolume = Math.min(Math.max(0, startVolume + volChange), maxVolume);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                    showSwipeIndicator(android.R.drawable.ic_lock_silent_mode_off, "Vol\n" + ((newVolume * 100) / maxVolume) + "%");
                } else if (isBrightness) {
                    float brightChange = deltaY / screenHeight;
                    float newBrightness = Math.min(Math.max(0.01f, startBrightness + brightChange), 1.0f);
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.screenBrightness = newBrightness;
                    getWindow().setAttributes(lp);
                    showSwipeIndicator(android.R.drawable.ic_menu_camera, "Light\n" + (int)(newBrightness * 100) + "%");
                }
                return false; 
            }
        });
    }

    private void showSwipeIndicator(int iconResId, String text) {
        swipeIndicatorLayout.setVisibility(View.VISIBLE);
        swipeIndicatorIcon.setImageResource(iconResId);
        swipeIndicatorText.setText(text);
    }

    private void hideSwipeIndicator() {
        swipeIndicatorLayout.postDelayed(new Runnable() {
            @Override public void run() { swipeIndicatorLayout.setVisibility(View.GONE); }
        }, 300);
    }

    @Override
    public void onBackPressed() {
        if (!isAppUnlocked) { super.onBackPressed(); return; }
        if (isFullscreen) {
            if (myWebView.getWebChromeClient() != null) ((WebChromeClient) myWebView.getWebChromeClient()).onHideCustomView();
            return;
        }
        if (myWebView.canGoBack()) myWebView.goBack();
        else super.onBackPressed();
    }
}
