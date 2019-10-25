package com.mindaxx.zhangp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import com.mindaxx.zhangp.util.IPutils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }


    public void onClick(View view) {

        String localIpAddress = IPutils.getLocalIpAddress();

        Log.e("IpAddress", "onClick: 1  " + localIpAddress);

        String getWIFIIpAddress = IPutils.getWIFIIpAddress(this);

        Log.e("IpAddress", "onClick: 2  " + getWIFIIpAddress);

        String getNetIp = IPutils.getNetIp();

        Log.e("IpAddress", "onClick: 3  " + getNetIp);


    }
}
