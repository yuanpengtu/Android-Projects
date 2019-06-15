package baidumapsdk.demo.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;
import baidumapsdk.demo.baidumap.BaiduUtils;
import baidumapsdk.demo.baidumap.LocationHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此demo用来展示如何结合定位SDK实现定位，
 * 并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * Created by hxw on 2017/4/24.
 */

public class LocationDemo extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.defaulticon)
    RadioButton defaulticon;
    @BindView(R.id.customicon)
    RadioButton customicon;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.button1)
    Button requestLocButton;

    private BaiduMap mBaiduMap;
    private LocationHelper locationHelper;
    private MyLocationConfiguration.LocationMode mCurrentMode;//定位图层显示方式
    private SensorManager mSensorManager;//传感器管理服务
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private Double lastX = 0.0;
    private float mCurrentDirection = 0.0f;//定位数据的方向信息
    private double mCurrentLat = 0.0;//纬度坐标
    private double mCurrentLon = 0.0;//经度坐标
    private float mCurrentAccracy = 0.0f;//定位精度
    private MyLocationData locData;
    private BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true; // 是否首次定位
    private BDLocationListener locationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();

            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                //地图移动到定位地点
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newMapStatus(new MapStatus.Builder()
                                .target(ll)
                                .zoom(18.0f)
                                .build()));
            }
            //打印定位信息
            BaiduUtils.showLocateInfo(location);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        init();
        initListener();
    }


    private void init() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText("普通");

        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        locationHelper = new LocationHelper(getApplication());//默认定位一次,不会反复定位
        //配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        locationHelper.setLocationOption(option);
        locationHelper.registerListener(locationListener);
        locationHelper.start();
    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.defaulticon:
                        // 传入null则，恢复默认图标
                        mCurrentMarker = null;
                        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
                        //，参见 setMyLocationEnabled(boolean)
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                        break;
                    case R.id.customicon:
                        // 修改为自定义marker
                        mCurrentMarker = BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_geo);
                        /**
                         * MyLocationConfiguration(MyLocationConfiguration.LocationMode mode,
                         * boolean enableDirection,
                         * BitmapDescriptor customMarker,
                         * int accuracyCircleFillColor,
                         * int accuracyCircleStrokeColor)
                         *
                         * mode - 定位图层显示方式, 默认为 LocationMode.NORMAL 普通态
                         * enableDirection - 是否允许显示方向信息
                         * customMarker - 设置用户自定义定位图标，可以为 null
                         * accuracyCircleFillColor - 设置精度圈填充颜色
                         * accuracyCircleStrokeColor - 设置精度圈填充颜色
                         */
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker,
                                accuracyCircleFillColor, accuracyCircleStrokeColor));

                        break;
                    default:
                        break;
                }
            }
        });

        requestLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                                .newMapStatus(new MapStatus.Builder()
                                        .overlook(0)
                                        .build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                                .newMapStatus(new MapStatus.Builder()
                                        .overlook(0)
                                        .build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (float) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        locationHelper.unregisterListener(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        // 退出时销毁定位
        locationHelper.stop();
        if (mCurrentMarker!=null){
            mCurrentMarker.recycle();
        }
    }
}
