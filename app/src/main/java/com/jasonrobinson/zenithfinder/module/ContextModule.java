package com.jasonrobinson.zenithfinder.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class ContextModule {

    private final Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides
    Context providesContext() {
        return mContext;
    }
}
