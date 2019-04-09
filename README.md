# Usage
Add in `build.gradle`
```
dependencies {
    // ...
    implementation 'com.github.BataevDaniil:appinapp-photos-android:d0b3ae57ca'
    // ...
}
```
and add
```
allprojects {
  repositories {
     // ...
	 maven { url 'https://jitpack.io' }
  }
}
```

In code
```
import codemonx.com.appinappphotosandroid.AppinappPhotosAndroid;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // open app with apikey "APP-123-123-123"
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setClass(this, AppinappPhotosAndroid.class);
        intent.putExtra("apiKey", "APP-123-123-123");
        startActivity(intent);
    }
}
```
