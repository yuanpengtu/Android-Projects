package baidumapsdk.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此Demo用来演示离线地图的下载和显示
 * Created by hxw on 2017/4/26.
 */

public class OfflineDemo extends AppCompatActivity {
    @BindView(R.id.cityid)
    TextView cidView;
    @BindView(R.id.city)
    EditText cityNameView;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.state)
    TextView stateView;
    @BindView(R.id.start)
    Button start;
    @BindView(R.id.stop)
    Button stop;
    @BindView(R.id.del)
    Button del;
    @BindView(R.id.clButton)
    Button clButton;
    @BindView(R.id.localButton)
    Button localButton;
    @BindView(R.id.city_list)
    LinearLayout cityList;
    @BindView(R.id.hotcitylist)
    ListView hotCityList;
    @BindView(R.id.allcitylist)
    ListView allCityList;
    @BindView(R.id.citylist_layout)
    LinearLayout citylistLayout;
    @BindView(R.id.localmaplist)
    ListView localMapListView;
    @BindView(R.id.localmap_layout)
    LinearLayout localmapLayout;

    private MKOfflineMap mOffline = null;//离线地图服务,用于管理离线地图.
    private LocalMapAdapter lAdapter = null;
    /**
     * 已下载的离线地图信息列表
     * MKOLUpdateElement 离线地图更新信息
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mOffline = new MKOfflineMap();
        mOffline.init(new MKOfflineMapListener() {//离线地图事件通知接口。
            //该接口返回新安装离线地图、下载更新、数据版本更新等结果，用户需要实现该接口以处理相应事件
            @Override
            public void onGetOfflineMapState(int type, int state) {
                switch (type) {
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE://离线地图下载更新事件类型
                        MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                        // 处理下载进度更新提示
                        if (update != null) {
                            stateView.setText(String.format("%s : %d%%", update.cityName,
                                    update.ratio));
                            updateView();
                        }
                        break;
                    case MKOfflineMap.TYPE_NEW_OFFLINE://新安装离线地图事件类型
                        // 有新离线地图安装
                        Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                        break;
                    case MKOfflineMap.TYPE_VER_UPDATE://离线地图数据版本更新事件类型
                        // 版本更新提示
                        //MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                        break;
                    default:
                        break;
                }
            }
        });//初使化

        ArrayList<String> hotCities = new ArrayList<String>();
        final ArrayList<String> hotCityNames = new ArrayList<String>();
        // 获取热门城市列表
        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records1) {//离线地图搜索城市记录结构
                hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
                hotCityNames.add(r.cityName);
            }
        }
        ListAdapter hAdapter = (ListAdapter) new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, hotCities);
        hotCityList.setAdapter(hAdapter);
        hotCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cityNameView.setText(hotCityNames.get(i));
            }
        });


        ArrayList<String> allCities = new ArrayList<String>();
        final ArrayList<String> allCityNames = new ArrayList<String>();
        // 获取所有支持离线地图的城市
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records2) {
                allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
                allCityNames.add(r.cityName);
            }
        }
        ListAdapter aAdapter = (ListAdapter) new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, allCities);
        allCityList.setAdapter(aAdapter);
        allCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cityNameView.setText(allCityNames.get(i));
            }
        });

        clickCityListButton();

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();//返回各城市离线地图更新信息
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);

        //搜索离线城市
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MKOLSearchRecord> records = mOffline
                        .searchCity(cityNameView.getText().toString());//根据城市名搜索该城市离线地图记录
                if (records == null || records.size() != 1) {
                    return;
                }
                cidView.setText(String.valueOf(records.get(0).cityID));
            }
        });

        //开始下载
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityid = Integer.parseInt(cidView.getText().toString());
                mOffline.start(cityid);//启动下载指定城市ID的离线地图，或在暂停更新某城市后继续更新下载某城市离线地图
                clickLocalMapListButton();
                Toast.makeText(OfflineDemo.this, "开始下载离线地图. cityid: " + cityid,
                        Toast.LENGTH_SHORT).show();
                updateView();
            }
        });

        //暂停下载
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityid = Integer.parseInt(cidView.getText().toString());
                mOffline.pause(cityid);//暂停下载或更新指定城市ID的离线地图
                Toast.makeText(OfflineDemo.this, "暂停下载离线地图. cityid: " + cityid,
                        Toast.LENGTH_SHORT).show();
                updateView();
            }
        });

        //删除离线地图
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityid = Integer.parseInt(cidView.getText().toString());
                mOffline.remove(cityid);//删除指定城市ID的离线地图
                Toast.makeText(OfflineDemo.this, "删除离线地图. cityid: " + cityid,
                        Toast.LENGTH_SHORT).show();
                updateView();
            }
        });

        clButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCityListButton();
            }
        });

        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLocalMapListButton();
            }
        });
    }

    /**
     * 更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();//返回各城市离线地图更新信息
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notifyDataSetChanged();
    }

    /**
     * 切换至下载管理列表
     */
    public void clickLocalMapListButton() {
        citylistLayout.setVisibility(View.GONE);
        localmapLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 切换至城市列表
     */
    public void clickCityListButton() {
        citylistLayout.setVisibility(View.VISIBLE);
        localmapLayout.setVisibility(View.GONE);
    }

    private String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    protected void onPause() {
        int cityid = Integer.parseInt(cidView.getText().toString());
        MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);//返回指定城市ID离线地图更新信息
        if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
            mOffline.pause(cityid);//暂停下载或更新指定城市ID的离线地图
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(OfflineDemo.this,
                    R.layout.item_offline_localmap, null);
            initViewItem(view, e);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
            Button display = (Button) view.findViewById(R.id.display);
            Button remove = (Button) view.findViewById(R.id.remove);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView update = (TextView) view.findViewById(R.id.update);
            TextView ratio = (TextView) view.findViewById(R.id.ratio);
            ratio.setText(e.ratio + "%");
            title.setText(e.cityName);
            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }
            if (e.ratio != 100) {
                display.setEnabled(false);
            } else {
                display.setEnabled(true);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mOffline.remove(e.cityID);
                    updateView();
                }
            });
            display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("customStyle", false);
                    intent.putExtra("x", e.geoPt.longitude);
                    intent.putExtra("y", e.geoPt.latitude);
                    intent.putExtra("level", 13.0f);
                    intent.setClass(OfflineDemo.this, BaseMapDemo.class);
                    startActivity(intent);
                }
            });
        }

    }
}
