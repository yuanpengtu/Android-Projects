package baidumapsdk.demo.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.UiSettings;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示地图UI控制功能
 * Created by hxw on 2017/4/24.
 */

public class UISettingDemo extends AppCompatActivity {

    @BindView(R.id.zoom)
    CheckBox cbZoom;
    @BindView(R.id.scroll)
    CheckBox cbScroll;
    @BindView(R.id.rotate)
    CheckBox cbRotate;
    @BindView(R.id.overlook)
    CheckBox cbOverlook;
    @BindView(R.id.compass)
    CheckBox cbCompass;
    @BindView(R.id.mappoi)
    CheckBox cbMappoi;
    @BindView(R.id.allGesture)
    CheckBox cbAllGestures;
    @BindView(R.id.setPadding)
    CheckBox setPadding;
    @BindView(R.id.bmapView)
    MapView mMapView;

    TextView mTextView;
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    private static final int paddingLeft = 0;
    private static final int paddingTop = 0;
    private static final int paddingRight = 0;
    private static final int paddingBottom = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uisetting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();

//        mMapView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 介绍获取比例尺的宽高，需在MapView绘制完成之后
//                int scaleControlViewWidth = mMapView.getScaleControlViewWidth();
//                int scaleControlViewHeight = mMapView.getScaleControlViewHeight();
//            }
//        }, 0);

        //是否启用缩放手势
        cbZoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUiSettings.setZoomGesturesEnabled(b);
            }
        });

        //是否启用平移手势
        cbScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUiSettings.setScrollGesturesEnabled(b);
            }
        });

        //是否启用旋转手势
        cbRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUiSettings.setRotateGesturesEnabled(b);
            }
        });

        //是否启用俯视手势
        cbOverlook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUiSettings.setOverlookingGesturesEnabled(b);
            }
        });

        //是否启用指南针图层
        cbCompass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mUiSettings.setCompassEnabled(b);
            }
        });

        //是否显示底图默认标注
        cbMappoi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBaiduMap.showMapPoi(b);
            }
        });

        //禁用所有手势
        cbAllGestures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //设置是否允许所有手势操作
                mUiSettings.setAllGesturesEnabled(!b);
            }
        });

        //设置Padding区域
        setPadding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBaiduMap.setViewPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    addView(mMapView);
                } else {
                    mBaiduMap.setViewPadding(0, 0, 0, 0);
                    mMapView.removeView(mTextView);
                }
            }
        });
    }

    private void addView(MapView mapView) {
        mTextView = new TextView(this);
        mTextView.setText(getText(R.string.instruction));
        mTextView.setTextSize(14.0f);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.BLACK);
        mTextView.setBackgroundColor(Color.parseColor("#AA00FF00"));

        mapView.addView(mTextView, new MapViewLayoutParams.Builder()
                .layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode)//指定 MapViewLayoutParams 的方式：屏幕坐标或者地图经纬度坐标
                //absoluteMode 指定子view 的屏幕坐标
                //mapMode 指定子View的经纬度坐标
                .width(mapView.getWidth())//指定 MapViewLayoutParams 的宽度
                .height(paddingBottom)//指定 MapViewLayoutParams 的高度
                .point(new Point(0, mapView.getHeight()))//指定 MapViewLayoutParams 的屏幕坐标
                .align(MapViewLayoutParams.ALIGN_LEFT, MapViewLayoutParams.ALIGN_BOTTOM)//指定 MapViewLayoutParams 的对齐方式，默认水平居中，垂直下对齐
                .build());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
    }
}
