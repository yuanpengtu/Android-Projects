package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 * Created by hxw on 2017/4/24.
 */

public class GeoCoderDemo extends AppCompatActivity {


    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.geocodekey)
    EditText geocodekey;
    @BindView(R.id.geocode)
    Button geocode;
    @BindView(R.id.lat)
    EditText lat;
    @BindView(R.id.lon)
    EditText lon;
    @BindView(R.id.reversegeocode)
    Button reversegeocode;
    @BindView(R.id.bmapView)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private GeoCoder mSearch = null; // 地理编码查询接口，也可去掉地图模块独立使用

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoder);
        ButterKnife.bind(this);

        init();
        initListener();
    }

    private void init() {
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块
        mSearch = GeoCoder.newInstance();
    }

    private void initListener() {
        reversegeocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng ptCenter = new LatLng((Float.valueOf(lat.getText().toString())),
                        (Float.valueOf(lon.getText().toString())));
                //发起反地理编码请求(经纬度->地址信息)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()//反地理编码请求参数
                        .location(ptCenter));//设置反地理编码位置坐标
            }
        });

        geocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发起地理编码(地址信息->经纬度)请求
                mSearch.geocode(new GeoCodeOption()//地理编码请求参数
                        .city(city.getText().toString())//设置城市
                        .address(geocodekey.getText().toString()));//设置地址
            }
        });
        //设置查询结果监听者
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            //地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(GeoCoderDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                mBaiduMap.clear();//清空地图所有的 Overlay 覆盖物以及 InfoWindow
                mBaiduMap.addOverlay(new MarkerOptions()
                        .position(result.getLocation())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory
                        .newLatLng(result.getLocation()));
                String strInfo = String.format("纬度：%f 经度：%f",
                        result.getLocation().latitude, result.getLocation().longitude);
                Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
            }

            //反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(GeoCoderDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                mBaiduMap.clear();
                mBaiduMap.addOverlay(new MarkerOptions()
                        .position(result.getLocation())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory
                        .newLatLng(result.getLocation()));
                Toast.makeText(GeoCoderDemo.this, result.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
        mSearch.destroy();//释放该地理编码查询对象
    }
}
