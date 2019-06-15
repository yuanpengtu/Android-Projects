package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

import baidumapsdk.demo.R;
import baidumapsdk.demo.baidumap.overlay.PoiOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示poi搜索功能
 * Created by hxw on 2017/4/24.
 */

public class PoiSearchDemo extends AppCompatActivity {
    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.searchkey)
    AutoCompleteTextView searchkey;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.searchNearby)
    Button searchNearby;
    @BindView(R.id.searchBound)
    Button searchBound;
    @BindView(R.id.map)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch;//POI检索接口
    private SuggestionSearch mSuggestionSearch;//建议查询接口

    private List<String> suggest;
    private ArrayAdapter<String> sugAdapter;
    private int loadIndex = 0;
    private int radius = 100;
    private int searchType = 0;  // 搜索的类型，在显示时区分 1:城市内搜索 2:周边搜索 3:区域搜索
    LatLng center = new LatLng(39.92235, 116.380338);
    LatLng southwest = new LatLng(39.92235, 116.380338);
    LatLng northeast = new LatLng(39.947246, 116.414977);
    LatLngBounds searchbound = new LatLngBounds.Builder()
            .include(southwest)
            .include(northeast)
            .build();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);
        ButterKnife.bind(this);

        init();
        initListener();
    }

    private void init() {
        mBaiduMap = mMapView.getMap();
        // 初始化Poi搜索模块
        mPoiSearch = PoiSearch.newInstance();
        // 初始化建议搜索模块
        mSuggestionSearch = SuggestionSearch.newInstance();

        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        searchkey.setAdapter(sugAdapter);
        searchkey.setThreshold(1);//输入一个字母就开始自动提示
    }

    private void initListener() {
        //设置poi检索监听者
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            //poi 查询结果回调
            //获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Toast.makeText(PoiSearchDemo.this, "未找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    mBaiduMap.clear();
                    PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();

                    switch (searchType) {
                        case 2:
                            showNearbyArea(center, radius);
                            break;
                        case 3:
                            showBound(searchbound);
                            break;
                        default:
                            break;
                    }

                    return;
                }
                if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {//检索词有岐义
                    // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                        strInfo += cityInfo.city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG)
                            .show();
                }
            }

            //poi 详情查询结果回调
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(PoiSearchDemo.this, poiDetailResult.getName() + ": " +
                            poiDetailResult.getAddress(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            //poi 室内检索结果回调
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        //设置建议请求结果监听器
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            //建议查询结果回调函数
            @Override
            public void onGetSuggestionResult(SuggestionResult result) {
                if (result == null || result.getAllSuggestions() == null) {
                    return;
                }
                suggest = new ArrayList<String>();
                for (SuggestionResult.SuggestionInfo info : result.getAllSuggestions()) {
                    if (info.key != null) {
                        suggest.add(info.key);
                    }
                }
                sugAdapter = new ArrayAdapter<String>(PoiSearchDemo.this, android.R.layout.simple_dropdown_item_1line, suggest);
                searchkey.setAdapter(sugAdapter);
                sugAdapter.notifyDataSetChanged();
            }
        });

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        searchkey.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 * 建议请求入口
                 */
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()//建议查询请求参数
                        .keyword(cs.toString())//指定建议关键字 必填
                        .city(city.getText().toString()));//设置建议请求城市 必填
                // .citylimit(java.lang.Boolean citylimit)设置是否限制城市范围
                // 选填 默认为false 取值为"true"时，仅返回city中指定城市检索结果。
                //.location(LatLng loction)指定位置 选填 设置位置之后，返回结果按距离该位置的远近进行排序。
            }
        });

        //响应城市内搜索按钮点击事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType = 1;
                //城市内检索
                mPoiSearch.searchInCity((new PoiCitySearchOption())//poi城市内检索参数
                        .city(city.getText().toString())//检索城市
                        .keyword(searchkey.getText().toString())//搜索关键字
                        .pageNum(loadIndex));//分页编号
            }
        });

        //响应周边搜索按钮点击事件
        searchNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType = 2;
                //周边检索
                mPoiSearch.searchNearby(new PoiNearbySearchOption()//附近检索参数
                        .keyword(searchkey.getText().toString())//检索关键字
                        .sortType(PoiSortType.distance_from_near_to_far)//搜索结果排序规则，可选，默认
                        .location(center)//检索位置
                        .radius(radius)//设置检索的半径范围
                        .pageNum(loadIndex));//分页编号
            }
        });

        //响应区域搜索按钮点击事件
        searchBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType = 3;
                //范围内检索
                mPoiSearch.searchInBound(new PoiBoundSearchOption()//POI范围内检索参数
                        .bound(searchbound)//poi检索范围
                        .keyword(searchkey.getText().toString())//检索关键字
                        .pageNum(loadIndex));//分页编号
            }
        });
    }

    /**
     * 对周边检索的范围进行绘制
     *
     * @param center
     * @param radius
     */
    public void showNearbyArea(LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions()
                .position(center)
                .icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);
        //画圆
        OverlayOptions ooCircle = new CircleOptions()
                .fillColor(0xCCCCCC00)
                .center(center)
                .stroke(new Stroke(5, 0xFFFF00FF))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
        centerBitmap.recycle();
    }

    /**
     * 对区域检索的范围进行绘制
     *
     * @param bounds
     */
    public void showBound(LatLngBounds bounds) {
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay);

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f);//设置 ground 覆盖物透明度
        mBaiduMap.addOverlay(ooGround);

        mBaiduMap.setMapStatus(MapStatusUpdateFactory
                .newLatLng(bounds.getCenter()));

        bdGround.recycle();
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
        mPoiSearch.destroy();//释放检索对象
        mSuggestionSearch.destroy();//释放对象资源
    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            //POI 详情检索
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(poi.uid));//欲检索的poi的uid
            // }
            return true;
        }
    }
}
