package baidumapsdk.demo.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示地图缩放，旋转，视角控制，单击，双击，长按，截图的事件响应
 * Created by hxw on 2017/4/22.
 */

public class MapControlDemo extends AppCompatActivity {
    @BindView(R.id.zoom_button)
    Button zoomButton;
    @BindView(R.id.zoom_level)
    EditText etZoomLevel;
    @BindView(R.id.rotate_button)
    Button rotateButton;
    @BindView(R.id.rotate_angle)
    EditText etRotateAngle;
    @BindView(R.id.overlook_button)
    Button overlookButton;
    @BindView(R.id.overlook_angle)
    EditText etOverlookAngle;
    @BindView(R.id.state)
    TextView mStateBar;
    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.update_status)
    Button updateStatus;
    @BindView(R.id.save_screen)
    Button saveScreen;

    private BaiduMap mBaiduMap;
    /**
     * 当前地点击点
     */
    private LatLng currentPt;

    private String touchType;
    //bitmap 描述信息
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapcontrol);
        ButterKnife.bind(this);

        mBaiduMap = mMapView.getMap();

        initListener();
    }

    /**
     * 对地图事件的消息响应
     */
    private void initListener() {
        //设置触摸地图事件监听者
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {

            }
        });

        //设置地图单击事件监听者
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            @Override
            public void onMapClick(LatLng point) {
                touchType = "单击地图";
                currentPt = point;
                updateMapState();
            }

            /**
             * 单击地图中的POI点
             */
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                touchType = "单击POI点";
                currentPt = mapPoi.getPosition();
                updateMapState();
                return false;
            }
        });

        //设置地图双击事件监听者
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                touchType = "长按";
                currentPt = point;
                updateMapState();
            }
        });

        //设置地图双击事件监听者
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClick(LatLng point) {
                touchType = "双击";
                currentPt = point;
                updateMapState();
            }
        });

        //地图状态发生变化
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                updateMapState();
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                updateMapState();
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                updateMapState();
            }
        });
        //缩放按钮事件
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performZoom();
                updateMapState();
            }
        });
        //旋转按钮事件
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRotate();
                updateMapState();
            }
        });
        //俯仰按钮事件
        overlookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performOverlook();
                updateMapState();
            }
        });
        //更新状态按钮事件
        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAll();
                updateMapState();
            }
        });
        //截图按钮事件
        saveScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapControlDemo.this, "正在截取屏幕图片...",
                        Toast.LENGTH_SHORT).show();
                saveScreen();

            }
        });

    }

    /**
     * 截图，在SnapshotReadyCallback中保存图片到 sd 卡
     */
    private void saveScreen() {
        //发起截图请求
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                File file = new File("/mnt/sdcard/test.png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(
                            Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                    Toast.makeText(MapControlDemo.this,
                            "屏幕截图成功，图片存在: " + file.toString(),
                            Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 处理缩放 sdk 缩放级别范围： [3.0,21.0]
     * V3.7.0起，地图支持缩放至21级显示；卫星图、热力图和交通路况图最高支持20级缩放显示。
     */
    private void performZoom() {
        try {
            float zoomLevel = Float.parseFloat(etZoomLevel.getText().toString());
            MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);//设置地图缩放级别
            mBaiduMap.animateMapStatus(u);//以动画方式更新地图状态，动画耗时 300 ms
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的缩放级别", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理旋转 旋转角范围： -180 ~ 180 , 单位：度 逆时针旋转
     */
    private void performRotate() {
        try {
            int rotateAngle = Integer.parseInt(etRotateAngle.getText().toString());
            //定义地图状态
            MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus())
                    .rotate(rotateAngle)//设置地图旋转角度，逆时针旋转。
                    .build();//创建地图状态对象
            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
            mBaiduMap.animateMapStatus(u);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的旋转角度", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理俯视 俯角范围： -45 ~ 0 , 单位： 度
     */
    private void performOverlook() {
        try {
            int overlookAngle = Integer.parseInt(etOverlookAngle.getText().toString());
            MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus())
                    .overlook(overlookAngle)//设置地图俯仰角
                    .build();
            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
            mBaiduMap.animateMapStatus(u);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的俯角", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理所有参数，改变地图状态
     */
    private void performAll() {
        try {
            float zoomLevel = Float.parseFloat(etZoomLevel.getText().toString());
            int rotateAngle = Integer.parseInt(etRotateAngle.getText().toString());
            int overlookAngle = Integer.parseInt(etOverlookAngle.getText().toString());
            MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus())
                    .rotate(rotateAngle)//设置地图旋转角度，逆时针旋转。
                    .zoom(zoomLevel)//设置地图缩放级别
                    .overlook(overlookAngle)//设置地图俯仰角
                    .build();//创建地图状态对象
            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
            mBaiduMap.animateMapStatus(u);//以动画方式更新地图状态，动画耗时 300 ms
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确参数，旋转角和俯角需为整数", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新地图状态显示面板
     */
    private void updateMapState() {
        if (mStateBar == null) {
            return;
        }
        String state = "";
        if (currentPt == null) {
            state = "点击、长按、双击地图以获取经纬度和地图状态";
        } else {
            state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
                    currentPt.longitude, currentPt.latitude);
            MarkerOptions ooA = new MarkerOptions().position(currentPt).icon(bdA);
            mBaiduMap.clear();//清空地图所有的 Overlay 覆盖物以及 InfoWindow
            mBaiduMap.addOverlay(ooA);//向地图添加一个 Overlay
        }
        state += "\n";
        MapStatus ms = mBaiduMap.getMapStatus();//获取地图的当前状态
        state += String.format(
                "zoom=%.1f rotate=%d overlook=%d",
                ms.zoom, (int) ms.rotate, (int) ms.overlook);
        mStateBar.setText(state);

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
        if (bdA!=null){
            bdA.recycle();
        }
    }

}
