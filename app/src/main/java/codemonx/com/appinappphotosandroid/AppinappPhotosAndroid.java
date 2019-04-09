package codemonx.com.appinappphotosandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AppinappPhotosAndroid extends AppCompatActivity {
    static String baseUrl = "http://dev-appinapp-photos-app.s3-website-us-east-1.amazonaws.com/?apiKey=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinapp_photos_android);

        WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        Bundle info = getIntent().getExtras();
        String url = info.getString("apiKey");

        if (url == null) {
            Log.e("AppinappPhotosAndroid", "url unspecified in bundle");
        } else {
            webView.loadUrl(AppinappPhotosAndroid.baseUrl + url);
        }
    }
}
