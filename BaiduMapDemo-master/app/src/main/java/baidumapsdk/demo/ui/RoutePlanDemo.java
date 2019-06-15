package baidumapsdk.demo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

import baidumapsdk.demo.R;
import baidumapsdk.demo.adapter.RouteLineAdapter;
import baidumapsdk.demo.baidumap.overlay.BikingRouteOverlay;
import baidumapsdk.demo.baidumap.overlay.DrivingRouteOverlay;
import baidumapsdk.demo.baidumap.overlay.MassTransitRouteOverlay;
import baidumapsdk.demo.baidumap.overlay.OverlayManager;
import baidumapsdk.demo.baidumap.overlay.TransitRouteOverlay;
import baidumapsdk.demo.baidumap.overlay.WalkingRouteOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 此demo用来展示如何进行驾车、步行、公交、骑行、跨城综合路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 * Created by hxw on 2017/4/25.
 */

public class RoutePlanDemo extends AppCompatActivity {

    @BindView(R.id.mass)
    Button mass;
    @BindView(R.id.drive)
    Button drive;
    @BindView(R.id.transit)
    Button transit;
    @BindView(R.id.walk)
    Button walk;
    @BindView(R.id.bike)
    Button bike;
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.customicon)
    Button customicon;
    @BindView(R.id.pre)
    Button mBtnPre;// 上一个节点
    @BindView(R.id.next)
    Button mBtnNext;// 下一个节点

    private BaiduMap mBaidumap;
    private TextView popupText = null; // 泡泡view
    // 搜索相关
    private RoutePlanSearch mSearch = null;    // 路径规划搜索接口，也可去掉地图模块独立使用
    private RouteLine route = null;//路线数据结构的基类,表示一条路线，路线可能包括：路线规划中的换乘/驾车/步行路线
    private OverlayManager routeOverlay = null;
    //表示一个跨城交通换乘路线，换乘路线将根据既定策略调配多种交通工具。
    //换乘路线可能包含：城市公交路段，地铁路段，步行路段，飞机，大巴
    private MassTransitRouteLine massroute = null;
    private WalkingRouteResult nowResultwalk = null;//表示步行路线结果
    private BikingRouteResult nowResultbike = null;//表示骑行路线结果
    private TransitRouteResult nowResultransit = null;//换乘路线结果
    private DrivingRouteResult nowResultdrive = null;//驾车路线结果
    private MassTransitRouteResult nowResultmass = null;//跨城公交线路规划结果

    private int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。0：跨城搜索 1：驾车搜索 2：公交搜索 3：步行搜索 4：骑行搜索
    private int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private boolean hasShownDialogue = false;
    private boolean useDefaultIcon = false;
    /**
     * 路径规划中的出行节点信息,出行节点包括：起点，终点，途经点
     * 出行节点信息可以通过两种方式确定：
     * <p>
     * 1： 给定出行节点经纬度坐标
     * <p>
     * 2： 给定出行节点地名和城市名
     * <p>
     * 设置起终点信息，对于tranist search 来说，城市名无意义
     */
    private PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗");
    private PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routeplan);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    private void initView() {
        mBaidumap = mMapView.getMap();
        //初始化路径规划搜索模块
        mSearch = RoutePlanSearch.newInstance();

        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

    }

    private void initListener() {
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaidumap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //设置路线检索监听者
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //步行路线结果回调
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);

                    if (result.getRouteLines().size() > 1) {
                        nowResultwalk = result;
                        if (!hasShownDialogue) {
                            MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanDemo.this,
                                    result.getRouteLines(),
                                    RouteLineAdapter.Type.WALKING_ROUTE);
                            myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    hasShownDialogue = false;
                                }
                            });
                            myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    route = nowResultwalk.getRouteLines().get(position);
                                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                                    mBaidumap.setOnMarkerClickListener(overlay);
                                    routeOverlay = overlay;
                                    overlay.setData(nowResultwalk.getRouteLines().get(position));
                                    overlay.addToMap();
                                    overlay.zoomToSpan();
                                }

                            });
                            myTransitDlg.show();
                            hasShownDialogue = true;
                        }
                    } else if (result.getRouteLines().size() == 1) {
                        // 直接显示
                        route = result.getRouteLines().get(0);
                        WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                        mBaidumap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();

                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }

                }
            }

            //换乘路线结果回调
            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);

                    if (result.getRouteLines().size() > 1) {
                        nowResultransit = result;
                        if (!hasShownDialogue) {
                            MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanDemo.this,
                                    result.getRouteLines(),
                                    RouteLineAdapter.Type.TRANSIT_ROUTE);
                            myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    hasShownDialogue = false;
                                }
                            });
                            myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    route = nowResultransit.getRouteLines().get(position);
                                    TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                                    mBaidumap.setOnMarkerClickListener(overlay);
                                    routeOverlay = overlay;
                                    overlay.setData(nowResultransit.getRouteLines().get(position));
                                    overlay.addToMap();
                                    overlay.zoomToSpan();
                                }

                            });
                            myTransitDlg.show();
                            hasShownDialogue = true;
                        }
                    } else if (result.getRouteLines().size() == 1) {
                        // 直接显示
                        route = result.getRouteLines().get(0);
                        TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                        mBaidumap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();

                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }
                }
            }

            //跨城公共交通路线结果回调
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点模糊，获取建议列表
                    result.getSuggestAddrInfo();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nowResultmass = result;

                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);

                    if (!hasShownDialogue) {
                        // 列表选择
                        MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanDemo.this,
                                result.getRouteLines(),
                                RouteLineAdapter.Type.MASS_TRANSIT_ROUTE);
                        nowResultmass = result;
                        myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                hasShownDialogue = false;
                            }
                        });
                        myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                            @Override
                            public void onItemClick(int position) {

                                MyMassTransitRouteOverlay overlay = new MyMassTransitRouteOverlay(mBaidumap);
                                mBaidumap.setOnMarkerClickListener(overlay);
                                routeOverlay = overlay;
                                massroute = nowResultmass.getRouteLines().get(position);
                                overlay.setData(nowResultmass.getRouteLines().get(position));

                                MassTransitRouteLine line = nowResultmass.getRouteLines().get(position);
                                overlay.setData(line);
                                if (nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId()) {
                                    // 同城
                                    overlay.setSameCity(true);
                                } else {
                                    // 跨城
                                    overlay.setSameCity(false);
                                }
                                mBaidumap.clear();
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }

                        });
                        myTransitDlg.show();
                        hasShownDialogue = true;
                    }
                }
            }

            //驾车路线结果回调
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;

                    if (result.getRouteLines().size() > 1) {
                        nowResultdrive = result;
                        if (!hasShownDialogue) {
                            MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanDemo.this,
                                    result.getRouteLines(),
                                    RouteLineAdapter.Type.DRIVING_ROUTE);
                            myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    hasShownDialogue = false;
                                }
                            });
                            myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    route = nowResultdrive.getRouteLines().get(position);
                                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                                    mBaidumap.setOnMarkerClickListener(overlay);
                                    routeOverlay = overlay;
                                    overlay.setData(nowResultdrive.getRouteLines().get(position));
                                    overlay.addToMap();
                                    overlay.zoomToSpan();
                                }

                            });
                            myTransitDlg.show();
                            hasShownDialogue = true;
                        }
                    } else if (result.getRouteLines().size() == 1) {
                        route = result.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                        routeOverlay = overlay;
                        mBaidumap.setOnMarkerClickListener(overlay);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        mBtnPre.setVisibility(View.VISIBLE);
                        mBtnNext.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }
                }
            }

            //室内路线规划回调
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            //骑行路线结果回调
            @Override
            public void onGetBikingRouteResult(BikingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    nodeIndex = -1;
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);

                    if (result.getRouteLines().size() > 1) {
                        nowResultbike = result;
                        if (!hasShownDialogue) {
                            MyTransitDlg myTransitDlg = new MyTransitDlg(RoutePlanDemo.this,
                                    result.getRouteLines(),
                                    RouteLineAdapter.Type.DRIVING_ROUTE);
                            myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    hasShownDialogue = false;
                                }
                            });
                            myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    route = nowResultbike.getRouteLines().get(position);
                                    BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                                    mBaidumap.setOnMarkerClickListener(overlay);
                                    routeOverlay = overlay;
                                    overlay.setData(nowResultbike.getRouteLines().get(position));
                                    overlay.addToMap();
                                    overlay.zoomToSpan();
                                }

                            });
                            myTransitDlg.show();
                            hasShownDialogue = true;
                        }
                    } else if (result.getRouteLines().size() == 1) {
                        route = result.getRouteLines().get(0);
                        BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                        routeOverlay = overlay;
                        mBaidumap.setOnMarkerClickListener(overlay);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        mBtnPre.setVisibility(View.VISIBLE);
                        mBtnNext.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("route result", "结果数<0");
                        return;
                    }

                }
            }
        });

        //跨城搜索
        mass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
                PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");
                //发起跨城公共路线检索
                mSearch.masstransitSearch(new MassTransitRoutePlanOption()//换乘路线规划参数
                        .from(stMassNode)//设置起点
                        .to(enMassNode));//设置终点
                nowSearchType = 0;
            }
        });

        //驾车搜索
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                //发起驾车路线规划
                mSearch.drivingSearch(new DrivingRoutePlanOption()//驾车路线规划参数
                        .from(stNode)
                        .to(enNode));
                nowSearchType = 1;
            }
        });

        //公交搜索
        transit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                //发起换乘路线规划
                mSearch.transitSearch(new TransitRoutePlanOption()//换乘路线规划参数
                        .from(stNode)
                        .city("北京")//设置换乘路线规划城市，起终点中的城市将会被忽略
                        .to(enNode));
                nowSearchType = 2;
            }
        });

        //步行搜索
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                //发起步行路线规划
                mSearch.walkingSearch(new WalkingRoutePlanOption()
                        .from(stNode)
                        .to(enNode));
                nowSearchType = 3;

            }
        });

        //骑行搜索
        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                //发起骑行路线规划
                mSearch.bikingSearch(new BikingRoutePlanOption()
                        .from(stNode)
                        .to(enNode));
                nowSearchType = 4;
            }
        });

        /**
         * 切换路线图标，刷新地图使其生效
         * 注意： 起终点图标使用中心对齐.
         */
        customicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (routeOverlay == null) {
                    return;
                }
                if (useDefaultIcon) {
                    customicon.setText("自定义起终点图标");
                    Toast.makeText(RoutePlanDemo.this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();
                } else {
                    customicon.setText("系统起终点图标");
                    Toast.makeText(RoutePlanDemo.this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();
                }
                useDefaultIcon = !useDefaultIcon;
                routeOverlay.addToMap();
            }
        });

    }

    /**
     * 重置界面数据
     */
    private void reset() {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
    }

    /**
     * 节点浏览示例
     */
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;
        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城搜索
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            //RouteNode 表示路线中的一节点，节点包括：路线起终点，公交站点等
            step = route.getAllStep()//获取路线中的所有路段
                    .get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance()//获取入口
                        .getLocation();//获取位置
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();//获取说明
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        } else if (nowSearchType == 0) {
            //跨城搜索
            if (massroute == null || massroute.getNewSteps() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            boolean isSamecity = nowResultmass.getOrigin()//获得起点
                    .getCityId() == nowResultmass.getDestination()//获得终点
                    .getCityId();
            int size = 0;
            if (isSamecity) {
                //是同城
                size = massroute.getNewSteps()//返回该线路的step信息
                        .size();
            } else {
                for (int i = 0; i < massroute.getNewSteps().size(); i++) {
                    size += massroute.getNewSteps().get(i).size();
                }
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < size - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            if (isSamecity) {
                // 同城
                step = massroute.getNewSteps().get(nodeIndex).get(0);
            } else {
                // 跨城
                int num = 0;
                for (int j = 0; j < massroute.getNewSteps().size(); j++) {
                    num += massroute.getNewSteps().get(j).size();
                    if (nodeIndex - num < 0) {
                        int k = massroute.getNewSteps().get(j).size() + nodeIndex - num;
                        step = massroute.getNewSteps().get(j).get(k);
                        break;
                    }
                }
            }

            nodeLocation = ((MassTransitRouteLine.TransitStep) step).getStartLocation();
            nodeTitle = ((MassTransitRouteLine.TransitStep) step).getInstructions();

        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RoutePlanDemo.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
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
        mSearch.destroy();//释放对象资源

    }

    /**
     * 定制RouteOverly
     */
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        public MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_transit);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }
}
