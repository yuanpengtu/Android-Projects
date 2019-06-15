package baidumapsdk.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;

import java.util.List;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hxw on 2017/4/25.
 */

public class DistrictSearchDemo extends AppCompatActivity {
    @BindView(R.id.city)
    EditText mCity;
    @BindView(R.id.district)
    EditText mDistrict;
    @BindView(R.id.districtSearch)
    Button districtSearch;
    @BindView(R.id.map)
    MapView mMapView;

    private BaiduMap mBaiduMap;
    private DistrictSearch mDistrictSearch;//行政区域检索接口
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_search_demo);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mBaiduMap = mMapView.getMap();
        mDistrictSearch = DistrictSearch.newInstance();

        districtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = "";
                String district = "";
                if (mCity.getText() != null && !"".equals(mCity.getText()) ) {
                    city = mCity.getText().toString();
                }
                if (mDistrict.getText() != null && !"".equals(mDistrict.getText()) ) {
                    district = mDistrict.getText().toString();
                }
                //行政区域检索入口
                mDistrictSearch.searchDistrict(new DistrictSearchOption()//行政区域检索请求参数
                        .cityName(city)//区域检索城市名称
                        .districtName(district));//区域检索的区名称
            }
        });

        //设置行政区域检索结果监听者
        mDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult) {
                mBaiduMap.clear();
                if (districtResult == null) {
                    return;
                }
                if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<List<LatLng>> polyLines = districtResult.getPolylines();//获取行政区域边界坐标点
                    if (polyLines == null) {
                        return;
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (List<LatLng> polyline : polyLines) {
                        OverlayOptions ooPolyline11 = new PolylineOptions()
                                .width(10)
                                .points(polyline)//设置折线坐标点列表
                                .dottedLine(true)
                                .color(0xAA00FF00);
                        mBaiduMap.addOverlay(ooPolyline11);
                        OverlayOptions ooPolygon = new PolygonOptions()
                                .points(polyline)//设置多边形坐标点列表
                                .stroke(new Stroke(5, 0xAA00FF88))
                                .fillColor(0xAAFFFF00);
                        mBaiduMap.addOverlay(ooPolygon);
                        for (LatLng latLng : polyline) {
                            builder.include(latLng);//让该地理范围包含一个地理位置坐标
                        }
                    }
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory
                            .newLatLngBounds(builder.build()));

                }
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
        mDistrictSearch.destroy();//释放检索对象资源
    }
}
