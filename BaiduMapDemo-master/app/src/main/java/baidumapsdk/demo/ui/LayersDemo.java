package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示地图图层显示的控制方法
 * Created by hxw on 2017/4/22.
 */

public class LayersDemo extends AppCompatActivity {

    @BindView(R.id.normal)
    RadioButton normal;
    @BindView(R.id.satellite)
    RadioButton satellite;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.traffic)
    CheckBox traffic;
    @BindView(R.id.baiduHeatMap)
    CheckBox baiduHeatMap;
    @BindView(R.id.bmapView)
    MapView mMapView;

    //定义 BaiduMap 地图对象的操作方法与接口
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layers);
        ButterKnife.bind(this);

        mBaiduMap = mMapView.getMap();

        initListener();
    }

    private void initListener() {
        //设置底图显示模式
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                switch (id) {
                    case R.id.normal:
                        //普通图
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.satellite:
                        //卫星tu
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        break;
                    default:
                        break;
                }
            }
        });

        //设置是否显示交通图
        traffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBaiduMap.setTrafficEnabled(b);
            }
        });

        //设置是否显示百度热力图
        baiduHeatMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBaiduMap.setBaiduHeatMapEnabled(b);
            }
        });
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
