package com.modoutech.wisdomparking.baidumap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.baidu.mapapi.model.LatLng
import com.baidu.navisdk.adapter.*
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanPreference.ROUTE_PLAN_MOD_RECOMMEND
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 用kotlin写的导航示例,注意APPID,在manifests的activity里设置android:theme="@android:style/Theme.Translucent.NoTitleBar"
 * @author hxw
 * @date 2017/11/24
 */
class BNReadlyActivity : Activity() {
//
//    private val authBaseRequestCode = 1
//    private val authComRequestCode = 2
//    private val authBaseArr = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
//    private val authComArr = arrayOf(Manifest.permission.READ_PHONE_STATE)
//
//    private val mSDCardPath: String? by lazy { getSdcardDir() }
//
//    private lateinit var dialog: MaterialDialog
//
//    /**
//     * 内部TTS播报状态回传handler
//     */
//    private val ttsHandler =
//            @SuppressLint("HandlerLeak")
//            object : Handler() {
//                override fun handleMessage(msg: Message) {
//                    val type = msg.what
//                    when (type) {
//                        BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG -> {
//                            Timber.d("TTS play start")
//                        }
//                        BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG -> {
//                            Timber.d("TTS play end")
//                        }
//                        else -> {
//                        }
//                    }
//                }
//            }
//    /**
//     * 内部TTS播报状态回调接口
//     */
//    private val ttsPlayStateListener = object : BaiduNaviManager.TTSPlayStateListener {
//
//        override fun playEnd() {
//            Timber.d("TTS play end")
//        }
//
//        override fun playStart() {
//            Timber.d("TTS play start")
//        }
//    }
//
//    private lateinit var sNode: BNRoutePlanNode
//    private lateinit var eNode: BNRoutePlanNode
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        val sl = intent.getParcelableExtra<LatLng>("start")
//        val el = intent.getParcelableExtra<LatLng>("end")
//
//        sNode = BNRoutePlanNode(sl.longitude, sl.latitude, intent.getStringExtra("startName"), null, BNRoutePlanNode.CoordinateType.BD09LL)
//        eNode = BNRoutePlanNode(el.longitude, el.latitude, intent.getStringExtra("endName"),
//                null, BNRoutePlanNode.CoordinateType.BD09LL)
//        dialog = MaterialDialog.Builder(this)
//                .content("请等待...")
//                .progress(true, 0)
//                .show()
//
//        BNOuterLogUtil.setLogSwitcher(true)
//        if (initDirs()) {
//            initNavi()
//        }
//    }
//
//    private fun initDirs(): Boolean {
//        if (mSDCardPath == null) {
//            return false
//        }
//        val f = File(mSDCardPath, packageName)
//        if (!f.exists()) {
//            try {
//                f.mkdir()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return false
//            }
//        }
//        return true
//    }
//
//    private fun initNavi() {
//        // 申请权限
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, authBaseArr[0]) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, authBaseArr, authBaseRequestCode)
//                return
//            }
//        }
//
//        BaiduNaviManager.getInstance().init(this, mSDCardPath, packageName, object : BaiduNaviManager.NaviInitListener {
//            override fun onAuthResult(status: Int, msg: String) {
//                if (status == 0) {
//                    Timber.d("onAuthResult: key校验成功!")
//                } else {
//                    Timber.d("onAuthResult: key校验失败, $msg")
//                }
//            }
//
//            override fun initStart() {
//                Timber.d("百度导航引擎初始化开始")
//            }
//
//            override fun initFailed() {
//                Timber.d("百度导航引擎初始化失败")
//            }
//
//            override fun initSuccess() {
//                Timber.d("百度导航引擎初始化成功")
//                initSetting()
//                routeplanToNavi()
//            }
//
//        }, null, ttsHandler, ttsPlayStateListener)
//    }
//
//
//    private fun getSdcardDir(): String?
//            = if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED, ignoreCase = true)) {
//        Environment.getExternalStorageDirectory().toString()
//    } else null
//
//    private fun initSetting() {
//        //日夜模式 1：自动模式 2：白天模式 3：夜间模式
//        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_AUTO)
//        //预览条显示:显示路况条
//        BNaviSettingManager
//                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON)
//        //语音播报模式:老手模式
//        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran)
//        //实时路况条设置:路况条 开
//        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON)
//
//        BNaviSettingManager.setIsAutoQuitWhenArrived(true)
//        val bundle = Bundle()
//        /** 必须设置APPID，否则会静音*/
//        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10263980")
//        BNaviSettingManager.setNaviSdkParam(bundle)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == authBaseRequestCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initNavi()
//            } else {
//                Toast.makeText(this@BNReadlyActivity, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        } else if (requestCode == authComRequestCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                routeplanToNavi()
//            } else {
//                Toast.makeText(this@BNReadlyActivity, "没有完备的权限!", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    private fun routeplanToNavi() {
//        if (!BaiduNaviManager.isNaviInited()) {
//            Toast.makeText(this@BNReadlyActivity, "还未初始化!", Toast.LENGTH_SHORT).show()
//            return
//        }
//        // 权限申请 保证导航功能完备
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, authComArr[0]) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, authComArr, authComRequestCode)
//                return
//            }
//        }
//
//        val list = arrayListOf(sNode, eNode)
//
//        //preference - 算路偏好， 参考BaiduNaviManager.RoutePlanPreference定义
//        //isGPSNav - true表示真实GPS导航，false表示模拟导航
//        //listener - 开始导航回调监听器，在该监听器里一般是进入导航过程页面
//        BaiduNaviManager.getInstance().launchNavigator(this, list,
//                ROUTE_PLAN_MOD_RECOMMEND, true,
//                object : BaiduNaviManager.RoutePlanListener {
//                    override fun onRoutePlanFailed() {
//                        Toast.makeText(this@BNReadlyActivity, "算路失败", Toast.LENGTH_SHORT).show()
//                        finish()
//                    }
//
//                    override fun onJumpToNavigator() {
//                        /*
//                         * 设置途径点以及resetEndNode会回调该接口
//                         */
//
//                        Observable.just(true)
//                                .delay(2, TimeUnit.SECONDS)
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe {
//                                    dialog.dismiss()
//                                    val intent = Intent(this@BNReadlyActivity, BNGuideActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                    }
//
//                }, { what, p1, p2, bundle -> })
//    }
}