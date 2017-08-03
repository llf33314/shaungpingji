package com.gt.doubledisplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gt.doubledisplay.base.BaseActivity;
import com.gt.doubledisplay.web.WebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("url","https://www.baidu.com/");
                Intent intent=new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
    }

    /*@OnClick(R.id.tv)
    public void onViewClicked(View v) {

    }*/
}
