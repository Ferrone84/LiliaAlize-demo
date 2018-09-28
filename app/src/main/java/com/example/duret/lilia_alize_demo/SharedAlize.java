package com.example.duret.lilia_alize_demo;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

import AlizeSpkRec.*;

class SharedAlize {
    private static SimpleSpkDetSystem alizeSystem;

    static SimpleSpkDetSystem getInstance(Context appContext) throws IOException, AlizeException {
        if (alizeSystem == null) {
            // We create a new spk det system with a config (extracted from the assets) and a directory where it can store files (models + temporary files)
            InputStream configAsset = appContext.getAssets().open("AlizeDefault.cfg");
            alizeSystem = new SimpleSpkDetSystem(configAsset, appContext.getFilesDir().getPath());
            configAsset.close();
        }
        return alizeSystem;
    }
}
