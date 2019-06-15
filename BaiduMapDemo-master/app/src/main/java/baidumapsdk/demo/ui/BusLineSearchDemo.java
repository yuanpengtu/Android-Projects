package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;
import baidumapsdk.demo.baidumap.overlay.BusLineOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此demo用来展示如何进行公交线路详情检索，并使用RouteOverlay在地图上绘制 同时展示如何浏览路线节点并弹出泡泡
 * Created by hxw on 2017/4/25.
 */

public class BusLineSearchDemo extends AppCompatActivity {
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.searchkey)
    EditText searchkey;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.nextline)
    Button nextline;
    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.pre)
    Button mBtnPre;
    @BindView(R.id.next)
    Button mBtnNext;

    /**
     * 城市公交信息(包含地铁信息)查询
     * 该接口用于查询整条公交线路信息
     * 公交换乘路线查询请参看 RoutePlanSearch
     */
    private BusLineSearch mBusLineSearch = null;
    private BaiduMap mBaiduMap = null;
    private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BusLineOverlay overlay; // 公交路线绘制对象

    private int nodeIndex = -2; // 节点索引,供浏览节点时使用
    private BusLineResult route = null; // 保存驾车/步行路线数据的变量，供浏览节点时使用 公共交通信息查询结果
    private List<String> busLineIDList = null;
    private int busLineIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busline);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    private void initView() {
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        mBaiduMap = mMapView.getMap();

        mSearch = PoiSearch.newInstance();
        mBusLineSearch = BusLineSearch.newInstance();

        busLineIDList = new ArrayList<String>();
        overlay = new BusLineOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);
    }

    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //发起检索
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busLineIDList.clear();
                busLineIndex = 0;
                mBtnPre.setVisibility(View.INVISIBLE);
                mBtnNext.setVisibility(View.INVISIBLE);
                // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
                // 城市内检索
                mSearch.searchInCity(new PoiCitySearchOption()
                        .city(city.getText().toString())//检索城市
                        .keyword(searchkey.getText().toString()));//搜索关键字
            }
        });

        nextline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchNextBusLine();
            }
        });

        mSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(BusLineSearchDemo.this, "抱歉，未找到结果",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                // 遍历所有poi，找到类型为公交线路的poi
                busLineIDList.clear();
                for (PoiInfo poi : result.getAllPoi()) {
                    if (poi.type == PoiInfo.POITYPE.BUS_LINE //poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路,
                            || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                        busLineIDList.add(poi.uid);//poi id 如果为isPano为true，可用此参数 调用街景组件PanoramaService类的requestPanoramaWithPoiUId方法检索街景数据
                    }
                }
                searchNextBusLine();
                route = null;
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        //设置公交详情检索结果监听者
        mBusLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener() {
            //公交信息查询结果回调函数
            @Override
            public void onGetBusLineResult(BusLineResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(BusLineSearchDemo.this, "抱歉，未找到结果",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mBaiduMap.clear();
                route = result;
                nodeIndex = -1;
                overlay.setData(result);
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                Toast.makeText(BusLineSearchDemo.this, result.getBusLineName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    //发起下一线路的检索
    private void searchNextBusLine() {
        if (busLineIndex >= busLineIDList.size()) {
            busLineIndex = 0;
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0) {
            //公交检索入口
            mBusLineSearch.searchBusLine((new BusLineSearchOption()//城市公交信息查询参数
                    .city(city.getText().toString())//设置查询城市
                    .uid(busLineIDList.get(busLineIndex))));//设置公交路线uid

            busLineIndex++;
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {

        if (nodeIndex < -1 || route == null
                || nodeIndex >= route.getStations().size()) {
            return;
        }
        TextView popupText = new TextView(this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xff000000);
        // 上一个节点
        if (mBtnPre.equals(v) && nodeIndex > 0) {
            // 索引减
            nodeIndex--;
        }
        // 下一个节点
        if (mBtnNext.equals(v) && nodeIndex < (route.getStations().size() - 1)) {
            // 索引加
            nodeIndex++;
        }
        if (nodeIndex >= 0) {
            // 移动到指定索引的坐标
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
                    .getStations()//获取所有公交站点信息
                    .get(nodeIndex)
                    .getLocation()));
            // 弹出泡泡
            popupText.setText(route.getStations().get(nodeIndex).getTitle());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, route.getStations()
                    .get(nodeIndex).getLocation(), 10));
        }
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
        mSearch.destroy();//释放检索对象
        mBusLineSearch.destroy();//释放检索对象资源
    }
}
