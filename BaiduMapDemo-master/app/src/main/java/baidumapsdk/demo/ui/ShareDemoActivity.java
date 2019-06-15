package baidumapsdk.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.PoiDetailShareURLOption;
import com.baidu.mapapi.search.share.RouteShareURLOption;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;

import baidumapsdk.demo.R;
import baidumapsdk.demo.baidumap.overlay.PoiOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示短串分享功能
 * Created by hxw on 2017/4/26.
 */

public class ShareDemoActivity extends AppCompatActivity {
    @BindView(R.id.poishore)
    Button poishore;
    @BindView(R.id.addrshare)
    Button addrshare;
    @BindView(R.id.foot)
    RadioButton foot;
    @BindView(R.id.cycle)
    RadioButton cycle;
    @BindView(R.id.car)
    RadioButton car;
    @BindView(R.id.bus)
    RadioButton bus;
    @BindView(R.id.routeMode)
    RadioGroup routeMode;
    @BindView(R.id.routeShare)
    Button routeShare;
    @BindView(R.id.bmapView)
    MapView mMapView;

    private BaiduMap mBaiduMap = null;
    private PoiSearch mPoiSearch = null; // POI检索接口，也可去掉地图模块独立使用
    //共享URL是指代表特定信息(poi详情/位置)的一条经过压缩的URL，该URL可通过任何途径传播。
    //当终端用户点击该URL时，该URL所代表的特定信息会通过百度地图(客户端/web)重新呈现
    private ShareUrlSearch mShareUrlSearch = null;//共享URL查询接口
    private GeoCoder mGeoCoder = null;//地理编码查询接口
    private Marker mAddrMarker = null;
    private RouteShareURLOption.RouteShareMode mRouteShareMode;
    private PlanNode startNode;
    private PlanNode enPlanNode;
    // 保存搜索结果地址
    private String currentAddr = null;
    // 搜索城市
    private String mCity = "北京";
    // 搜索关键字
    private String searchKey = "餐馆";
    // 反地理编译点坐标
    private LatLng mPoint = new LatLng(40.056878, 116.308141);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_demo_activity);
        ButterKnife.bind(this);

        init();
        initListener();
    }

    private void init() {
        mBaiduMap = mMapView.getMap();
        mPoiSearch = PoiSearch.newInstance();
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mGeoCoder = GeoCoder.newInstance();
        mRouteShareMode = RouteShareURLOption.RouteShareMode.FOOT_ROUTE_SHARE_MODE;//步行短串分享
    }

    private void initListener() {
        poishore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发起poi搜索
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city(mCity)
                        .keyword(searchKey));
                Toast.makeText(ShareDemoActivity.this, "在" + mCity + "搜索 " + searchKey,
                        Toast.LENGTH_SHORT).show();
            }
        });

        addrshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发起反地理编码请求
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(mPoint));
                Toast.makeText(ShareDemoActivity.this,
                        String.format("搜索位置： %f，%f", mPoint.latitude, mPoint.longitude),
                        Toast.LENGTH_SHORT).show();
            }
        });

        routeMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.foot:
                        mRouteShareMode = RouteShareURLOption
                                .RouteShareMode.FOOT_ROUTE_SHARE_MODE;//步行短串分享
                        break;
                    case R.id.cycle:
                        mRouteShareMode = RouteShareURLOption
                                .RouteShareMode.CYCLE_ROUTE_SHARE_MODE;//骑行短串分享
                        break;
                    case R.id.car:
                        mRouteShareMode = RouteShareURLOption
                                .RouteShareMode.CAR_ROUTE_SHARE_MODE;//驾车短串分享
                        break;
                    case R.id.bus:
                        mRouteShareMode = RouteShareURLOption
                                .RouteShareMode.BUS_ROUTE_SHARE_MODE;//公交短串分享
                        break;
                    default:
                        break;
                }
            }
        });

        routeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNode = PlanNode.withLocation(new LatLng(40.056885, 116.308142));
                enPlanNode = PlanNode.withLocation(new LatLng(39.921933, 116.488962));
                //路线规划短串分享
                mShareUrlSearch.requestRouteShareUrl(new RouteShareURLOption()
                        .from(startNode)//设置起点
                        .to(enPlanNode)//设置终点
                        .routMode(mRouteShareMode));//设置模式
            }
        });

        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(ShareDemoActivity.this, "抱歉，未找到结果",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mBaiduMap.clear();
                PoiShareOverlay poiOverlay = new PoiShareOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.setData(result);
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        //设置共享URL结果监听
        mShareUrlSearch.setOnGetShareUrlResultListener(new OnGetShareUrlResultListener() {
            //poi详情分享URL结果回调
            @Override
            public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {
                // 分享短串结果
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个POI点详情: " + currentAddr
                        + " -- " + result.getUrl());
                it.setType("text/plain");
                startActivity(Intent.createChooser(it, "将短串分享到"));
            }

            //位置分享URL结果回调
            @Override
            public void onGetLocationShareUrlResult(ShareUrlResult result) {
                // 分享短串结果
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个位置: " + currentAddr
                        + " -- " + result.getUrl());
                it.setType("text/plain");
                startActivity(Intent.createChooser(it, "将短串分享到"));
            }

            //路线规划分享URL结果回调
            @Override
            public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一条路线，URL "
                        + " -- " + shareUrlResult.getUrl());
                it.setType("text/plain");
                startActivity(Intent.createChooser(it, "将短串分享到"));
            }
        });

        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(ShareDemoActivity.this, "抱歉，未找到结果",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mBaiduMap.clear();
                mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker == mAddrMarker) {
                            mShareUrlSearch
                                    .requestLocationShareUrl(new LocationShareURLOption()
                                            .location(marker.getPosition())//共享点位置
                                            .snippet("测试分享点")//通过短URL调起客户端时作为附加信息显示在名称下面
                                            .name(marker.getTitle()));//共享点名称
                        }
                        return true;
                    }
                });
                mAddrMarker = (Marker) mBaiduMap.addOverlay(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka))
                        .title(result.getAddress()).position(result.getLocation()));

                mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newMapStatus(new MapStatus.Builder()
                                .target(mAddrMarker.getPosition())
                                .build()));
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
        mGeoCoder.destroy();//释放该地理编码查询对象
        mShareUrlSearch.destroy();
        mPoiSearch.destroy();
    }

    /**
     * 使用PoiOverlay 展示poi点，在poi被点击时发起短串请求.
     */
    private class PoiShareOverlay extends PoiOverlay {

        public PoiShareOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            PoiInfo info = getPoiResult().getAllPoi().get(i);
            currentAddr = info.address;
            //请求poi详情分享URL
            mShareUrlSearch.requestPoiDetailShareUrl(new PoiDetailShareURLOption()
                    .poiUid(info.uid));//设置欲分享的poi的uid
            return true;
        }
    }
}
