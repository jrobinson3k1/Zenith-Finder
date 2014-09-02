package com.jasonrobinson.zenithfinder.module;

import android.content.Context;
import android.hardware.SensorManager;

import com.jasonrobinson.zenithfinder.ui.SensorFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = SensorFragment.class,
        includes = ContextModule.class
)
public class SensorModule {

    @Provides
    @Singleton
    SensorManager providesSensorManager(Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
}
