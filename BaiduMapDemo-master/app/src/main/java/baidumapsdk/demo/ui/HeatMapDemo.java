package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 热力图功能demo   //--使用用户自定义热力图数据
 * Created by hxw on 2017/4/24.
 */

public class HeatMapDemo extends AppCompatActivity {
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.remove)
    Button remove;
    @BindView(R.id.mapview)
    MapView mMapView;

    private HeatMap heatmap;//热力图瓦片提供者
    private BaiduMap mBaiduMap;//定义 BaiduMap 地图对象的操作方法与接口
    private boolean isDestroy;
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isDestroy) {
                mBaiduMap.addHeatMap(heatmap);
            }
            add.setEnabled(false);
            remove.setEnabled(true);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(5));

        add.setEnabled(false);
        remove.setEnabled(false);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHeatMap();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heatmap.removeHeatMap();
                add.setEnabled(true);
                remove.setEnabled(false);
            }
        });

        addHeatMap();
    }

    /**
     * 添加热力地图
     */
    private void addHeatMap() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                List<LatLng> data = getLocations();
                heatmap = new HeatMap.Builder()
                        .data(data)//设置热力图绘制的数据，data 或 weightedData接口必须设置其中之一
                        .build();
                h.sendEmptyMessage(0);
            }
        }.start();
    }

    private List<LatLng> getLocations() {
        List<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        String json = new Scanner(inputStream)
                .useDelimiter("\\A")
                .next();
        JSONArray array;
        try {
            array = new JSONArray(json);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject object = array.getJSONObject(i);
                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
                list.add(new LatLng(lat, lng));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
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
        isDestroy = true;
    }

}
