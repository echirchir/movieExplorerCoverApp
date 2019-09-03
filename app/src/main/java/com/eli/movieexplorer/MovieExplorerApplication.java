package com.eli.movieexplorer;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MovieExplorerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("coverapp.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
