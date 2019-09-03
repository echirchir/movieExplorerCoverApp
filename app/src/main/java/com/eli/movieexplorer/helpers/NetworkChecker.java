package com.eli.movieexplorer.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

@SuppressWarnings("WeakerAccess")
public class NetworkChecker {

    private final Context context;

    public NetworkChecker(Context context){
        this.context = context;
    }

    public boolean isConnected(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            @SuppressWarnings("deprecation")
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }

        }
        return false;
    }
}