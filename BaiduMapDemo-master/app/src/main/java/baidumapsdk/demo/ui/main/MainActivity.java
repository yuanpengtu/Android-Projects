package baidumapsdk.demo.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;

import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;
import baidumapsdk.demo.ui.BaseMapDemo;
import baidumapsdk.demo.ui.BusLineSearchDemo;
import baidumapsdk.demo.ui.DistrictSearchDemo;
import baidumapsdk.demo.ui.GeoCoderDemo;
import baidumapsdk.demo.ui.GeometryDemo;
import baidumapsdk.demo.ui.HeatMapDemo;
import baidumapsdk.demo.ui.LayersDemo;
import baidumapsdk.demo.ui.LocationDemo;
import baidumapsdk.demo.ui.MapControlDemo;
import baidumapsdk.demo.ui.MapFragmentDemo;
import baidumapsdk.demo.ui.MarkerClusterDemo;
import baidumapsdk.demo.ui.MultiMapViewDemo;
import baidumapsdk.demo.ui.OfflineDemo;
import baidumapsdk.demo.ui.OverlayDemo;
import baidumapsdk.demo.ui.PoiSearchDemo;
import baidumapsdk.demo.ui.RoutePlanDemo;
import baidumapsdk.demo.ui.ShareDemo;
import baidumapsdk.demo.ui.UISettingDemo;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hxw on 2017/4/22.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.text_Info)
    TextView text;
    @BindView(R.id.listView)
    ListView mListView;
    private List<DemoInfo> list;
    private SDKReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        initView();

        initReceiver();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<>();
        list.add(new DemoInfo(R.string.demo_title_basemap,
                R.string.demo_desc_basemap,
                BaseMapDemo.class));
        list.add(new DemoInfo(R.string.demo_title_map_fragment, R.string.demo_desc_map_fragment,
                MapFragmentDemo.class));
        list.add(new DemoInfo(R.string.demo_title_layers, R.string.demo_desc_layers,
                LayersDemo.class));
        list.add(new DemoInfo(R.string.demo_title_multimap,
                R.string.demo_desc_multimap,
                MultiMapViewDemo.class));
        list.add(new DemoInfo(R.string.demo_title_control,
                R.string.demo_desc_control,
                MapControlDemo.class));
        list.add(new DemoInfo(R.string.demo_title_ui, R.string.demo_desc_ui,
                UISettingDemo.class));
        list.add(new DemoInfo(R.string.demo_title_location, R.string.demo_desc_location,
                LocationDemo.class));
        list.add(new DemoInfo(R.string.demo_title_geometry, R.string.demo_desc_geometry,
                GeometryDemo.class));
        list.add(new DemoInfo(R.string.demo_title_overlay, R.string.demo_desc_overlay,
                OverlayDemo.class));
        list.add(new DemoInfo(R.string.demo_title_heatmap, R.string.demo_desc_heatmap,
                HeatMapDemo.class));
        list.add(new DemoInfo(R.string.demo_title_geocode, R.string.demo_desc_geocode,
                GeoCoderDemo.class));
        list.add(new DemoInfo(R.string.demo_title_poi, R.string.demo_desc_poi,
                PoiSearchDemo.class));
        list.add(new DemoInfo(R.string.demo_title_route, R.string.demo_desc_route,
                RoutePlanDemo.class));
        list.add(new DemoInfo(R.string.demo_title_districsearch, R.string.demo_desc_districsearch,
                DistrictSearchDemo.class));
        list.add(new DemoInfo(R.string.demo_title_bus, R.string.demo_desc_bus,
                BusLineSearchDemo.class));
        list.add(new DemoInfo(R.string.demo_title_share, R.string.demo_desc_share,
                ShareDemo.class));
        list.add(new DemoInfo(R.string.demo_title_offline, R.string.demo_desc_offline,
                OfflineDemo.class));
        list.add(new DemoInfo(R.string.demo_title_cluster, R.string.demo_desc_cluster,
                MarkerClusterDemo.class));
    }

    /**
     * 初始化界面
     */
    private void initView() {
        text.setTextColor(Color.BLACK);
        text.setText("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());

        setTitle(getTitle() + " v" + VersionInfo.getApiVersion());

        mListView.setAdapter(new MainListAdapter(MainActivity.this, list));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                onListItemClick(index);
            }
        });

    }

    private void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(MainActivity.this, list.get(index).getDemoClass());
        this.startActivity(intent);
    }

    /**
     * 初始化广播接受器
     */
    private void initReceiver() {
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);

        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(TAG, "action: " + s);
            text.setTextColor(Color.RED);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                text.setText("key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        + " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                text.setText("key 验证成功! 功能可以正常使用");
                text.setTextColor(Color.BLUE);
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                text.setText("网络出错");
            }
        }
    }
}
