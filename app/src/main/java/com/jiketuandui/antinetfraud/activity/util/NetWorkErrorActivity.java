package com.jiketuandui.antinetfraud.activity.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jiketuandui.antinetfraud.R;
import com.jiketuandui.antinetfraud.Util.StatusBarUtil;

/**
 * 2016年11月2日 20:58:20
 * 作为错误提示显示的Activity
 */
public class NetWorkErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work_error);
        StatusBarUtil.StatusBarLightMode(this);
    }

}
