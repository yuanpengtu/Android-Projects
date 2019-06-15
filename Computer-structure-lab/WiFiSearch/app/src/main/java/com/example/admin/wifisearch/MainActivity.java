package com.example.admin.wifisearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String wifiName;  //wifi名字
    private String macAddress;  //mac地址
    private int rssi;  //rssi的值
    private String apName;  //AP名字
    private WifiManager wifiManager;  //WiFi管理
    ArrayList<ScanResult> list;    //存放周围wifi热点对象的列表
    List<WifiBean> wifiBeanList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
    }

    /**
     * 获取WiFi名称、强度、Mac地址、AP地址
     */
    private void getInfo(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo info = wifiManager.getConnectionInfo();
        boolean started=wifiManager.startScan();  //开始扫描AP
        Toast.makeText(this,"started:"+Boolean.toString(started),Toast.LENGTH_SHORT).show();


        list = (ArrayList<ScanResult>) wifiManager.getScanResults();
        ListView listView = (ListView) findViewById(R.id.listView);  //获得界面的列表
        wifiBeanList = new ArrayList<>();
        if (list == null) {
            Toast.makeText(this, "当前周围无WiFi", Toast.LENGTH_LONG).show();
        }else {
            for(int i=0;i<list.size();i++) {
                wifiName = list.get(i).SSID;
                rssi = list.get(i).level;
                macAddress = info.getMacAddress();
                String top = "WiFi名称："+wifiName+"   RSSI："+rssi+"";
                String bottom = "MAC地址："+list.get(i).BSSID+"";
                wifiBeanList.add(new WifiBean(R.drawable.wifi,top,bottom));
            }
            listView.setAdapter(new MyAdapter(this,wifiBeanList));
        }
    }
}
