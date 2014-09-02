package com.jasonrobinson.zenithfinder;

import android.app.Application;

import com.jasonrobinson.zenithfinder.module.ContextModule;
import com.jasonrobinson.zenithfinder.module.GraphHolder;
import com.jasonrobinson.zenithfinder.module.SensorModule;

public class ZenithApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GraphHolder.getInstance().addModules(new ContextModule(this), new SensorModule());
    }
}
