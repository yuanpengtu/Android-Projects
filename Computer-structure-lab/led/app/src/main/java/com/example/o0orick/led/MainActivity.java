package com.example.o0orick.led;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Switch> SwitchList;
    public int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchList = new ArrayList<Switch>();
        SwitchList.add((Switch) findViewById(R.id.switch1));
        SwitchList.add((Switch) findViewById(R.id.switch2));
        SwitchList.add((Switch) findViewById(R.id.switch3));
        SwitchList.add((Switch) findViewById(R.id.switch4));

        SwitchList.get(0).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(0,1);
                }else {
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(0,0);

                }
            }
        });

        SwitchList.get(1).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(1,1);
                }else {
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(1,0);
                }
            }
        });

        SwitchList.get(2).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(2,1);
                }else {
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(2,0);
                }
            }
        });

        SwitchList.get(3).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(3,1);
                }else {
                    com.friendlyarm.AndroidSDK.HardwareControler.setLedState(3,0);
                }
            }
        });

    }
}
