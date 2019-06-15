package baidumapsdk.demo.baidumap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.modoutech.wisdomparking.baidumap.BNReadlyActivity;

import java.util.List;

/**
 * Created by hxw on 2017/4/11.
 */

public class BaiduUtils {
    //百度地图坐标系类型
    public final static String CoorType_GCJ02 = "gcj02";
    public final static String CoorType_BD09LL = "bd09ll";
    public final static String CoorType_BD09MC = "bd09";
    public static BDLocation location = new BDLocation();

    public static void startInNavi(Activity activity, LatLng start, LatLng end, String startName, String endName) {
        Intent intent = new Intent(activity, BNReadlyActivity.class);
        intent.putExtra("start", start);
        intent.putExtra("end", end);
        intent.putExtra("startName", startName);
        intent.putExtra("endName", endName);
        activity.startActivity(intent);
    }

    /**
     * 启动百度地图导航(Native)
     *
     * @param context
     * @param pointX      纬度
     * @param pointY      经度
     * @param parkingName 目的名
     */
    public static void startNavi(Context context, double pointX, double pointY, String parkingName) {
        LatLng pt1 = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng pt2 = new LatLng(pointX, pointY);

        // 构建 导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(pt1).endPoint(pt2)
                .startName(location.getPoiList().get(0).getName()).endName(parkingName);

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog(context);
        }
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public static void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    public static void showLocateInfo(BDLocation location) {
        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        sb.append("time : ");
        sb.append(location.getTime());//获取定位时间
        sb.append("\nerror code : ");
        sb.append(location.getLocType());//获取类型类型
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());//获取纬度信息
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());//获取经度信息
        sb.append("\nradius : ");
        sb.append(location.getRadius());//获取定位精准度,半径
        sb.append("\nCountryCode : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\nCountry : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\ncitycode : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\ncity : ");// 城市
        sb.append(location.getCity());
        sb.append("\nDistrict : ");// 区
        sb.append(location.getDistrict());
        sb.append("\nStreet : ");// 街道
        sb.append(location.getStreet());
        sb.append("\naddr : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\nDirection(not all devices have value): ");
        sb.append(location.getDirection());// 获取方向信息，单位度
        sb.append("\nlocationdescribe: ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        sb.append("\nPoi: ");// POI信息
        List<Poi> list = location.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        if (location.getLocType() == BDLocation.TypeGpsLocation) {
            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());//获取卫星数
            sb.append("\nheight : ");
            sb.append(location.getAltitude());//获取海拔高度信息，单位米
            sb.append("\ngps status : ");
            sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            // 网络定位结果
            if (location.hasAltitude()) {// *****如果有海拔高度*****
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
            }
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }

        Log.d("BaiduLocation", "showLocateInfo: " + sb.toString());
    }
}
