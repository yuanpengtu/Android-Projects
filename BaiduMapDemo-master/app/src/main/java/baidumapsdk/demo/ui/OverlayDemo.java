package baidumapsdk.demo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示覆盖物的用法
 * Created by hxw on 2017/4/24.
 */

public class OverlayDemo extends AppCompatActivity {
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.resert)
    Button resert;
    @BindView(R.id.alphaBar)
    SeekBar alphaBar;
    @BindView(R.id.animation)
    CheckBox animation;
    @BindView(R.id.bmapView)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，
    //也可以设置一个 BitmapDescriptor 作为该窗口的内容。
    private InfoWindow mInfoWindow;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);
    private BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markb);
    private BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markc);
    private BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markd);
    private BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private BitmapDescriptor bdGround = BitmapDescriptorFactory
            .fromResource(R.drawable.ground_overlay);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14.0f));

        initOverlay();
    }

    private void initListener() {

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
        // 重新添加Overlay
        resert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                initOverlay();
            }
        });
        alphaBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float alpha = ((float) seekBar.getProgress()) / 10;
                if (mMarkerA != null) {
                    mMarkerA.setAlpha(alpha);
                }
                if (mMarkerB != null) {
                    mMarkerB.setAlpha(alpha);
                }
                if (mMarkerC != null) {
                    mMarkerC.setAlpha(alpha);
                }
                if (mMarkerD != null) {
                    mMarkerD.setAlpha(alpha);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //设置 Marker 拖拽事件监听者
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            //Marker 被拖拽的过程中。
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            //Marker 拖拽结束
            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(OverlayDemo.this,
                        "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                                + marker.getPosition().longitude,
                        Toast.LENGTH_LONG).show();
            }

            //开始拖拽 Marker
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
        });
        //设置地图 Marker 覆盖物点击事件监听者,自3.4.0版本起可设置多个监听对象，
        //停止监听时调用removeMarkerClickListener移除监听对象
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                if (marker == mMarkerA || marker == mMarkerD) {
                    button.setText("更改位置");
                    button.setTextColor(Color.BLACK);
                    button.setWidth(300);

                    listener = new InfoWindow.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick() {
                            LatLng ll = marker.getPosition();
                            LatLng llNew = new LatLng(ll.latitude + 0.005,
                                    ll.longitude + 0.005);
                            marker.setPosition(llNew);//设置 Marker 覆盖物的位置坐标
                            mBaiduMap.hideInfoWindow();//隐藏当前 InfoWindow
                        }
                    };
                    /**
                     * bd - InfoWindow 展示的bitmap
                     * position - InfoWindow 显示的地理位置
                     * yOffset - InfoWindow Y 轴偏移量
                     * listener - InfoWindow 点击监听者
                     */
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                            .fromView(button), marker.getPosition(), -47, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);//显示 InfoWindow
                } else if (marker == mMarkerB) {
                    button.setText("更改图标");
                    button.setTextColor(Color.BLACK);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marker.setIcon(bd);
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    mInfoWindow = new InfoWindow(button, marker.getPosition(), -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarkerC) {
                    button.setText("删除");
                    button.setTextColor(Color.BLACK);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marker.remove();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    mInfoWindow = new InfoWindow(button, marker.getPosition(), -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }

                return true;
            }
        });
    }

    /**
     * 清除所有Overlay
     */
    private void clear() {
        mBaiduMap.clear();
        mMarkerA = null;
        mMarkerB = null;
        mMarkerC = null;
        mMarkerD = null;
    }

    /**
     * 初始化覆盖物
     */
    public void initOverlay() {
        // add marker overlay
        LatLng llA = new LatLng(39.963175, 116.400244);
        LatLng llB = new LatLng(39.942821, 116.369199);
        LatLng llC = new LatLng(39.939723, 116.425541);
        LatLng llD = new LatLng(39.906965, 116.401394);

        MarkerOptions ooA = new MarkerOptions()
                .position(llA)//设置 marker 覆盖物的位置坐标
                .icon(bdA)//设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
                .zIndex(9)//设置 marker 覆盖物的 zIndex
                .draggable(true);//设置 marker 是否允许拖拽，默认不可拖拽
        if (animation.isChecked()) {
            // 掉下动画
            ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        }
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

        MarkerOptions ooB = new MarkerOptions()
                .position(llB)
                .icon(bdB)
                .zIndex(5);
        if (animation.isChecked()) {
            // 掉下动画
            ooB.animateType(MarkerOptions.MarkerAnimateType.drop);
        }
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));

        MarkerOptions ooC = new MarkerOptions()
                .position(llC)
                .icon(bdC)
                .perspective(false)//设置是否开启 marker 覆盖物近大远小效果，默认开启
                .anchor(0.5f, 0.5f)//设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
                .rotate(30)//设置 marker 覆盖物旋转角度，逆时针
                .zIndex(7);
        if (animation.isChecked()) {
            // 生长动画
            ooC.animateType(MarkerOptions.MarkerAnimateType.grow);
        }
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));

        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdC);
        MarkerOptions ooD = new MarkerOptions()
                .position(llD)
                .icons(giflist)//设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
                .zIndex(0)
                .period(10);//设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快
        if (animation.isChecked()) {
            // 生长动画
            ooD.animateType(MarkerOptions.MarkerAnimateType.grow);
        }
        mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));

        // add ground overlay
        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        //地理范围数据结构，由西南以及东北坐标点确认
        LatLngBounds bounds = new LatLngBounds.Builder()//地理范围构造器
                .include(northeast)//让该地理范围包含一个地理位置坐标
                .include(southwest)//让该地理范围包含一个地理位置坐标
                .build();

        OverlayOptions ooGround = new GroundOverlayOptions()//构造 ground 覆盖物的选项类
                .positionFromBounds(bounds)//设置 ground 覆盖物的位置信息方式二，设置西南与东北坐标范围
                .image(bdGround)//设置 Ground 覆盖物的图片信息
                .transparency(0.8f);//设置 ground 覆盖物透明度
        mBaiduMap.addOverlay(ooGround);


        mBaiduMap.setMapStatus(MapStatusUpdateFactory
                .newLatLng(bounds.getCenter()));


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
        // 回收 bitmap 资源
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        bdD.recycle();
        bd.recycle();
        bdGround.recycle();
    }

}
