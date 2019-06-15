package com.modoutech.wisdomparking.baidumap

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.baidu.navisdk.adapter.BNRouteGuideManager

/**
 * 对于导航模块有两种方式来实现发起导航。 1：使用通用接口来实现 2：使用传统接口来实现
 * 本次采用传统接口实现,通用接口看百度的例子吧
 * @author hxw
 * @date 2017/11/24
 */
class BNGuideActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = BNRouteGuideManager.getInstance().onCreate(this,
                object : BNRouteGuideManager.OnNavigationListener {
                    override fun onNaviGuideEnd() {
                        //退出导航
                        finish()
                    }

                    override fun notifyOtherAction(actionType: Int, arg1: Int, arg2: Int, obj: Any) {
                        if (actionType == 0) {
                            //导航到达目的地 自动退出
//                            Timber.i("notifyOtherAction actionType = $actionType,导航到达目的地！")
                        }

//                        Timber.i("actionType:$actionType,arg1:$arg1,arg2:$arg2,obj:" + obj.toString())
                    }

                })

        if (view != null) {
            view.systemUiVisibility = View.INVISIBLE//隐藏状态栏
            setContentView(view)
        }
    }

    override fun onStart() {
        super.onStart()
        BNRouteGuideManager.getInstance().onStart()
    }

    override fun onResume() {
        super.onResume()
        BNRouteGuideManager.getInstance().onResume()
    }

    override fun onPause() {
        super.onPause()
        BNRouteGuideManager.getInstance().onPause()
    }

    override fun onStop() {
        super.onStop()
        BNRouteGuideManager.getInstance().onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        BNRouteGuideManager.getInstance().onDestroy()
    }

    /**
     * @see android.app.Activity#onBackPressed()
     * 此处onBackPressed传递false表示强制退出，true表示返回上一级，非强制退出
     */
    override fun onBackPressed() {
        super.onBackPressed()
        BNRouteGuideManager.getInstance().onBackPressed(false)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig)
    }
}