package com.vwo.mobile;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;

/**
 * Created by aman on 28/06/17.
 */

public class Initializer {
    private final VWO vwo;
    private final String apiKey;
    private final boolean optOut;

    Initializer(VWO vwo, String apiKey, boolean optOut) {
        this.vwo = vwo;
        this.apiKey = apiKey;
        this.optOut = optOut;
    }

    /**
     * Launches VWO sdk in Async mode.
     * <p>
     * This method will initialize the SDK either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     * </p>
     *
     * @deprecated Use {@link Initializer#launch(VWOStatusListener)} instead.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE})
    @Deprecated
    public void launch() {
        if (vwo == null) {
            throw new IllegalArgumentException("You need to initialize vwo instance first");
        }
        setup(null);
        vwo.startVwoInstance();
    }

    /**
     * Launches VWO sdk in Async mode with callback
     * <p>
     * This method will initialize the SDK either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     * </p>
     *
     * @param statusListener is the listener for receiving callback launch status update. i.e. Failure
     *                       or success.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE})
    public void launch(@Nullable VWOStatusListener statusListener) {
        setup(null);
        if (statusListener != null) {
            VWO.setVWOStatusListener(statusListener);
        }
        vwo.startVwoInstance();
    }

    /**
     * Start VWO sdk in sync mode(Not recommended. because it blocks UI thread for fetching data).
     * <p>
     * This method will initialize the sdk either by fetching data from server or
     * from data of previous launch or from defaults(in case of network failure)
     * </p>
     *
     * @param timeout is the timeout(in Milliseconds) for the HTTP call made to server.
     */
    @RequiresPermission(allOf = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE})
    public void launchSynchronously(long timeout) {
        setup(timeout);
        vwo.startVwoInstance();
    }

    /**
     * Set initialization {@link VWOConfig} for the launch.
     *
     * @param vwoConfig is the SDK launch config
     * @return the current {@link Initializer} object.
     */
    public Initializer config(@NonNull VWOConfig vwoConfig) {
        if (vwo.getConfig() != null) {
            VWOLog.w(VWOLog.CONFIG_LOGS, "Configuration already set", true);
        }
        this.vwo.setConfig(vwoConfig);
        return this;
    }

    private void setup(Long timeout) {
        if (this.vwo.getConfig() == null) {
            VWOConfig vwoConfig = new VWOConfig
                    .Builder()
                    .apiKey(apiKey)
                    .setOptOut(optOut)
                    .build();
            this.vwo.setConfig(vwoConfig);
        } else {
            VWOConfig vwoConfig = this.vwo.getConfig();
            vwoConfig.setApiKey(apiKey);
            vwoConfig.setOptOut(optOut);
        }

        this.vwo.getConfig().setTimeout(timeout);
    }
}