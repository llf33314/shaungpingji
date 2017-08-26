package com.gt.doubledisplay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.web.GTWebViewFrameLayout;
import com.gt.doubledisplay.web.WebViewActivity;
import com.gt.doubledisplay.web.WebViewDiffDisplayPresentation;

public class MainActivity extends BaseActivity {
    WebViewDiffDisplayPresentation mp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              /*  Bundle bundle= new Bundle();
                bundle.putString("url","https://www.baidu.com/");
                Intent intent=new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);*/

                /*DisplayManager mDisplayManager;//屏幕管理类
                mDisplayManager = (DisplayManager)MainActivity.this.getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays =mDisplayManager.getDisplays();

                Intent intent = new Intent();
                intent.putExtra(GTWebViewFrameLayout.PARAM_URL, GTWebViewFrameLayout.DEMO_URL);
                intent.putExtra(GTWebViewFrameLayout.PARAM_MODE, GTWebViewFrameLayout.MODE_SONIC);
                intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());

                DisplayManager mDisplayManager;//屏幕管理类
                mDisplayManager = (DisplayManager)MainActivity.this.getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays =mDisplayManager.getDisplays();
                if(mp==null){
                     mp= new WebViewDiffDisplayPresentation(MainActivity.this,displays[0],intent);
                }
                mp.show();*/
                /*if (displays.length<2){
                ToastUtil.getInstance().showToast("副屏未连接");
                }*/
    }


        });

      /*  if (hasPermission()) {
            init();
        } else {
            requestPermission();
        }*/
    }

   /* private void init() {
        // init sonic engine
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
    }


    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, GTWebViewFrameLayout.PERMISSION_REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length<1){
            return;
        }
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                init();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startBrowserActivity(int mode) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(GTWebViewFrameLayout.PARAM_URL, GTWebViewFrameLayout.DEMO_URL);
        intent.putExtra(GTWebViewFrameLayout.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        startActivityForResult(intent, -1);
    }*/

}
