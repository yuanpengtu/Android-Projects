package baidumapsdk.demo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import baidumapsdk.demo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 演示MapView的基本用法
 * 百度地图的个性化地图不能直接把map写在xml里
 * Created by hxw on 2017/4/22.
 */

public class BaseMapDemo extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = BaseMapDemo.class.getSimpleName();
    @BindView(R.id.rb_open)
    RadioButton rbOpen;
    @BindView(R.id.rb_close)
    RadioButton rbClose;
    @BindView(R.id.rg_group)
    RadioGroup rgGroup;
    @BindView(R.id.fr_layout)
    FrameLayout frLayout;

    private boolean mEnableCustomStyle = true;//是否开启个性化地图
    //用于设置个性化地图的样式文件
    // 提供三种样式模板："custom_config_blue.txt"，"custom_config_dark.txt"，"custom_config_midnightblue.txt"
    private static String PATH = "custom_config_dark.txt";

    private LatLng center = new LatLng(39.915071, 116.403907); // 默认 天安门 中心位置
    private float zoom = 11.0f; // 默认 11级
    private MapView mMapView;
    FrameLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_map);
        ButterKnife.bind(this);

        initData();
        initMap();
        initView();

    }

    // 初始化View
    private void initView() {
        //添加mapView在最底层
        frLayout.addView(mMapView, 0);


        if (mEnableCustomStyle) {
            rbOpen.setChecked(true);
        } else {
            rbClose.setChecked(true);
        }

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_open:
                        //设置个性化地图样式是否生效
                        MapView.setMapCustomEnable(true);
                        break;
                    case R.id.rb_close:
                        MapView.setMapCustomEnable(false);
                        break;
                    default:
                        break;

                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            mEnableCustomStyle = intent.getBooleanExtra("customStyle", true);
            center = new LatLng(intent.getDoubleExtra("y", 39.915071),
                    intent.getDoubleExtra("x", 116.403907));
            zoom = intent.getFloatExtra("level", 11.0f);
        }

        setMapCustomFile(this, PATH);
    }

    /**
     * 地图初始化
     */
    private void initMap() {
        mMapView = new MapView(this, new BaiduMapOptions());
        //设置个性化地图样式是否生效
        MapView.setMapCustomEnable(true);
    }

    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//      设置自定义地图样式的文件路径
//      该方法需在MapView/TextureMapView构造之前设置,所以用代码取构造界面了
//      对于复杂的界面,应在xml中写好,add进去mapview。
        MapView.setCustomMapStylePath(moduleName + "/" + PATH);

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
    }
}
