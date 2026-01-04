package snoof.app.garticphone;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        myWebView = findViewById(R.id.garticWebView);

        // Performance & Hardware acceleration
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportZoom(false); // Eliminates click delay
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectColorWheelObserver();
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadUrl("https://garticphone.com");
    }

    private void injectColorWheelObserver() {
        String script = "var script = document.createElement('script');" +
                "script.src = 'https://cdn.jsdelivr.net/npm/@jaames/iro@5';" +
                "script.onload = function() {" +
                "   const observer = new MutationObserver(() => {" +
                "       document.querySelectorAll('input[type=\"color\"]').forEach(input => {" +
                "           if (!input.dataset.handled) {" +
                "               input.dataset.handled = 'true';" +
                "               input.onclick = function(e) {" +
                "                   e.preventDefault();" +
                "                   e.stopImmediatePropagation();" +
                "                   showIroPicker(this);" +
                "               };" +
                "           }" +
                "       });" +
                "   });" +
                "   observer.observe(document.body, { childList: true, subtree: true });" +
                "};" +
                "document.head.appendChild(script);" +
                "" +
                "function triggerNativeEvent(element, value) {" +
                "   const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                "   nativeInputValueSetter.call(element, value);" +
                "   element.dispatchEvent(new Event('input', { bubbles: true }));" +
                "   element.dispatchEvent(new Event('change', { bubbles: true }));" +
                "}" +
                "" +
                "function showIroPicker(input) {" +
                "   var old = document.getElementById('iro-picker-container');" +
                "   if(old) document.body.removeChild(old);" +
                "   " +
                "   /* Fix: Start at White instead of Black if default value is detected */" +
                "   var startingColor = (input.value === '#000000') ? '#ffffff' : input.value;" +
                "   " +
                "   var container = document.createElement('div');" +
                "   container.id = 'iro-picker-container';" +
                "   container.style = 'position:fixed; top:50%; left:50%; transform:translate(-50%,-50%); z-index:1000000; background:#222; padding:20px; border-radius:20px; box-shadow:0 0 40px rgba(0,0,0,0.9); border: 2px solid #444; text-align:center;';" +
                "   document.body.appendChild(container);" +
                "   " +
                "   var cp = new iro.ColorPicker(container, {" +
                "       width: 240, " +
                "       color: startingColor, " +
                "       layout: [" +
                "           { component: iro.ui.Wheel }," +
                "           { component: iro.ui.Slider, options: { sliderType: 'saturation' } }," +
                "           { component: iro.ui.Slider, options: { sliderType: 'value' } }" +
                "       ]" +
                "   });" +
                "   " +
                "   cp.on('color:change', function(color) {" +
                "       triggerNativeEvent(input, color.hexString);" +
                "   });" +
                "   " +
                "   var btn = document.createElement('button');" +
                "   btn.innerText = 'SELECT';" +
                "   btn.style = 'margin-top:15px; width:100%; height:45px; background:#5cb85c; color:white; border:none; border-radius:10px; font-weight:bold; font-size:16px;';" +
                "   btn.onclick = () => document.body.removeChild(container);" +
                "   container.appendChild(btn);" +
                "}";

        myWebView.evaluateJavascript(script, null);
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}