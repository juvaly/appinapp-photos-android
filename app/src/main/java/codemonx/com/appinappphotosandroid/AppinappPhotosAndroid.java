package codemonx.com.appinappphotosandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AppinappPhotosAndroid extends AppCompatActivity {
    private static String baseUrl = "http://dev-appinapp-photos-app.s3-website-us-east-1.amazonaws.com/?apiKey=";
    private static String injectJavaScriptAboutEventPressBackButton = "window.callBackAndroidBackButtonPressed && window.callBackAndroidBackButtonPressed();";
    private static final int REQUEST_ID = 1;
    private ValueCallback<Uri[]> filePathCallback;
    private static final String nameFolderForSavePhotoFromCamera = "AppinappPhotosAndroid";
    private Uri pathImageFromCamera;
    WebView webView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ID) {
                Uri data = intent.getData();
                if (data == null) {
                    results = new Uri[]{pathImageFromCamera};
                } else {
                    results = new Uri[]{data};
                }
            }
        }
        filePathCallback.onReceiveValue(results);
        filePathCallback = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinapp_photos_android);

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new ViewClient());
        webView.setWebChromeClient(new ChromeClient());
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

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(AppinappPhotosAndroid.injectJavaScriptAboutEventPressBackButton, null);
            return;
        }

        try {
            webView.loadUrl("javascript:" + URLEncoder.encode(AppinappPhotosAndroid.injectJavaScriptAboutEventPressBackButton, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
    }

    public class ChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (AppinappPhotosAndroid.this.filePathCallback != null) {
                AppinappPhotosAndroid.this.filePathCallback.onReceiveValue(null);
            }
            AppinappPhotosAndroid.this.filePathCallback = filePathCallback;

            // open file system
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
            galleryIntent.setType("image/*");

            // open camera
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), AppinappPhotosAndroid.nameFolderForSavePhotoFromCamera);

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            AppinappPhotosAndroid.this.pathImageFromCamera = Uri.fromFile(file);
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, AppinappPhotosAndroid.this.pathImageFromCamera);

            Intent chooser = Intent.createChooser(galleryIntent, "Choose images");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePicture});
            startActivityForResult(chooser, REQUEST_ID);

            return true;
        }
    }
}
