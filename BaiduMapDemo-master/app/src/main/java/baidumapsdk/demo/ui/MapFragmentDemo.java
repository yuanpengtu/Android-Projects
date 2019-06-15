package baidumapsdk.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;

/**
 * Created by hxw on 2017/4/22.
 */

public class MapFragmentDemo extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = MapFragmentDemo.class.getSimpleName();

    SupportMapFragment mapFragment;
    //MapView 初始化选项
    private BaiduMapOptions mapOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        initData();
        initFragment();
    }

    private void initData() {
        Intent intent = getIntent();
        MapStatus.Builder builder = new MapStatus.Builder();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
            builder.target(p);
        }
        builder.overlook(-20).zoom(15);

        mapOptions = new BaiduMapOptions()
                .mapStatus(builder.build())//设置地图初始化时的地图状态， 默认地图中心点为北京天安门，缩放级别为 12.0f
                .compassEnabled(false)//设置是否允许指南针，默认允许。
                .zoomControlsEnabled(false);//设置是否显示缩放控件
    }

    private void initFragment() {
        mapFragment=SupportMapFragment.newInstance(mapOptions);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.map, mapFragment, "map_fragment").commit();
    }
}
