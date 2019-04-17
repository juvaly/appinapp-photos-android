package codemonx.com.appinappphotosandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AppinappPhotosAndroid extends AppCompatActivity {
    private static String baseUrl = "http://dev-appinapp-photos-app.s3-website-us-east-1.amazonaws.com/?apiKey=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinapp_photos_android);

        WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new ViewClient());
        webView.addJavascriptInterface(new JSInterface(), "Android");

        Bundle info = getIntent().getExtras();
        String url = info.getString("apiKey");

        if (url == null) {
            Log.e("AppinappPhotosAndroid", "url unspecified in bundle");
        } else {
            webView.loadUrl(AppinappPhotosAndroid.baseUrl + url);
        }
    }

    public class JSInterface {
        @JavascriptInterface
        public void close(String command) {
            if (command.equals("close")) {
                AppinappPhotosAndroid.this.finish();
            }

        }
    }

    public class ViewClient extends WebViewClient {
        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AppinappPhotosAndroid.this);
            builder.setTitle("No Internet connection. Please try again later!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppinappPhotosAndroid.this.finish();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
