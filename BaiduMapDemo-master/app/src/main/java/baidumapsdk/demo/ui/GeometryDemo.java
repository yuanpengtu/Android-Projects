package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此demo用来展示如何在地图上用GraphicsOverlay添加点、线、多边形、圆
 * 并对Polyline进行点击事件响应
 * 同时展示如何在地图上用TextOverlay添加文字
 * Created by hxw on 2017/4/24.
 */

public class GeometryDemo extends AppCompatActivity {

    @BindView(R.id.dottedline)
    CheckBox dottedLine;
    @BindView(R.id.button1)
    Button clearBtn;
    @BindView(R.id.button2)
    Button resetBtn;
    @BindView(R.id.bmapView)
    MapView mMapView;

    private BitmapDescriptor mRedTexture = BitmapDescriptorFactory
            .fromAsset("icon_road_red_arrow.png");
    private BitmapDescriptor mBlueTexture = BitmapDescriptorFactory
            .fromAsset("icon_road_blue_arrow.png");
    private BitmapDescriptor mGreenTexture = BitmapDescriptorFactory
            .fromAsset("icon_road_green_arrow.png");
    // 普通折线，点击时改变宽度
    private Polyline mPolyline;
    // 多颜色折线，点击时消失
    private Polyline mColorfulPolyline;
    // 纹理折线，点击时获取折线上点数及width
    private Polyline mTexturePolyline;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geometry);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearClick();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetClick();
            }
        });

        dottedLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mPolyline == null) {
                    return;
                }
                if (isChecked) {
                    mPolyline.setDottedLine(true);//设置折线是否虚线
                } else {
                    mPolyline.setDottedLine(false);
                }
            }
        });

        // 点击polyline的事件响应
        mBaiduMap.setOnPolylineClickListener(new BaiduMap.OnPolylineClickListener() {
            @Override
            public boolean onPolylineClick(Polyline polyline) {
                if (polyline == mPolyline) {
                    polyline.setWidth( 20 );
                } else if (polyline == mColorfulPolyline) {
                    polyline.remove();
                } else if (polyline == mTexturePolyline) {
                    Toast.makeText( getApplicationContext(), "点数：" + polyline.getPoints().size()
                                    + ",width:" + polyline.getWidth(),
                            Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        // 界面加载时添加绘制图层
        addCustomElementsDemo();

    }

    public void clearClick() {
        // 清除所有图层
        mMapView.getMap().clear();
    }

    public void resetClick() {
        dottedLine.setChecked(false);
        clearClick();
        // 添加绘制元素
        addCustomElementsDemo();
    }

    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo() {
        // 添加普通折线绘制
        LatLng p1 = new LatLng(39.97923, 116.357428);
        LatLng p2 = new LatLng(39.94923, 116.397428);
        LatLng p3 = new LatLng(39.97923, 116.437428);
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        //OverlayOptions 地图覆盖物选型基类
        OverlayOptions ooPolyline = new PolylineOptions()//创建折线覆盖物选项类
                .width(10)//设置折线线宽， 默认为 5， 单位：像素
                .color(0xAAFF0000)//设置折线颜色
                .points(points);//设置折线坐标点列表
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);


        // 添加多颜色分段的折线绘制
        LatLng p11 = new LatLng(39.965, 116.444);
        LatLng p21 = new LatLng(39.925, 116.494);
        LatLng p31 = new LatLng(39.955, 116.534);
        LatLng p41 = new LatLng(39.905, 116.594);
        LatLng p51 = new LatLng(39.965, 116.644);
        List<LatLng> points1 = new ArrayList<LatLng>();
        points1.add(p11);
        points1.add(p21);
        points1.add(p31);
        points1.add(p41);
        points1.add(p51);
        List<Integer> colorValue = new ArrayList<Integer>();
        colorValue.add(0xAAFF0000);
        colorValue.add(0xAA00FF00);
        colorValue.add(0xAA0000FF);
        OverlayOptions ooPolyline1 = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points1)
                //设置折线每个点的颜色值，每一个点带一个颜色值，
                //绘制时按照索引依次取值 颜色个数 >= points的个数，若colors越界大于点个数，
                //则取最后一个颜色绘制
                .colorsValues(colorValue);
        mColorfulPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline1);

        // 添加多纹理分段的折线绘制
        LatLng p111 = new LatLng(39.865, 116.444);
        LatLng p211 = new LatLng(39.825, 116.494);
        LatLng p311 = new LatLng(39.855, 116.534);
        LatLng p411 = new LatLng(39.805, 116.594);
        List<LatLng> points11 = new ArrayList<LatLng>();
        points11.add(p111);
        points11.add(p211);
        points11.add(p311);
        points11.add(p411);
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(mRedTexture);
        textureList.add(mBlueTexture);
        textureList.add(mGreenTexture);
        List<Integer> textureIndexs = new ArrayList<Integer>();
        textureIndexs.add(0);
        textureIndexs.add(1);
        textureIndexs.add(2);
        OverlayOptions ooPolyline11 = new PolylineOptions()
                .width(20)
                .points(points11)
                .dottedLine(true)//设置折线是否虚线
                .customTextureList(textureList)//设置折线多纹理分段绘制的纹理队列
                //设置折线每个点的纹理索引，
                //每一个点带一个索引，绘制时按照索引从customTextureList里面取，
                //个数>= points的个数 若index越界大于纹理列表个数，则取最后一个纹理绘制
                .textureIndex(textureIndexs);
        mTexturePolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline11);

        // 添加弧线
        OverlayOptions ooArc = new ArcOptions()//弧线构造选项
                .color(0xAA00FF00)//设置弧线的颜色
                .width(4)//设置弧线的线宽
                .points(p1, p2, p3);//设置弧线的起点、中点、终点坐标
        mBaiduMap.addOverlay(ooArc);

        // 添加圆
        LatLng llCircle = new LatLng(39.90923, 116.447428);
        OverlayOptions ooCircle = new CircleOptions()//创建圆的选项
                .fillColor(0x000000FF)//设置圆填充颜色
                .center(llCircle)//设置圆心坐标
                .stroke(new Stroke(5, 0xAA000000))//设置圆边框信息,边框的宽度和边框的颜色
                .radius(1400);//设置圆半径
        mBaiduMap.addOverlay(ooCircle);

        // 加圆点
        LatLng llDot = new LatLng(39.98923, 116.397428);
        OverlayOptions ooDot = new DotOptions()//创建圆点的选项类
                .center(llDot)//设置圆点的圆心坐标
                .radius(6)//设置圆点的半径，单位：像素, 默认为 5px
                .color(0xFF0000FF);//设置圆点的颜色
        mBaiduMap.addOverlay(ooDot);

        // 添加多边形
        LatLng pt1 = new LatLng(39.93923, 116.357428);
        LatLng pt2 = new LatLng(39.91923, 116.327428);
        LatLng pt3 = new LatLng(39.89923, 116.347428);
        LatLng pt4 = new LatLng(39.89923, 116.367428);
        LatLng pt5 = new LatLng(39.91923, 116.387428);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        pts.add(pt5);
        OverlayOptions ooPolygon = new PolygonOptions()//创建多边形覆盖物选项类
                .points(pts)//设置多边形坐标点列表
                .stroke(new Stroke(5, 0xAA00FF00))//设置多边形边框信息
                .fillColor(0xAAFFFF00);//设置多边形填充颜色
        mBaiduMap.addOverlay(ooPolygon);

        // 添加文字
        LatLng llText = new LatLng(39.86923, 116.397428);
        OverlayOptions ooText = new TextOptions()//创建文字覆盖物选项
                .bgColor(0xAAFFFF00)//设置文字覆盖物背景颜色
                .fontSize(24)//设置文字覆盖物字体大小
                .fontColor(0xFFFF00FF)//设置文字覆盖物字体颜色，默认黑色
                .text("百度地图SDK")//设置文字覆盖物的文字内容
                .rotate(-30)//设置文字覆盖物旋转角度，逆时针
                .position(llText);//设置文字覆盖物地理坐标
        mBaiduMap.addOverlay(ooText);
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
        if (mRedTexture != null) {
            mRedTexture.recycle();
        }
        if (mBlueTexture != null) {
            mBlueTexture.recycle();
        }
        if (mGreenTexture != null) {
            mGreenTexture.recycle();
        }
    }
}
