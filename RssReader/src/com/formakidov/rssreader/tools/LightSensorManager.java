package com.formakidov.rssreader.tools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightSensorManager implements SensorEventListener {
	 
    private enum Environment {DAY, NIGHT}
 
    public interface EnvironmentChangedListener {
        void onDayDetected();
        void onNightDetected();
    }
 
    private static final float SMOOTHING = 10;
    private static final int THRESHOLD_DAY_LUX = 50;
    private static final int THRESHOLD_NIGHT_LUX = 40;
    private static final String TAG = "LightSensorManager";
 
    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private EnvironmentChangedListener environmentChangedListener;
    private Environment currentEnvironment;
    private final LowPassFilter lowPassFilter;
 
    public LightSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lowPassFilter = new LowPassFilter(SMOOTHING);
    }
 
    public void enable() {
        if (lightSensor != null){
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.w(TAG, "Light sensor in not supported");
        }
    }
 
    public void disable() {
        sensorManager.unregisterListener(this);
    }
 
    public EnvironmentChangedListener getEnvironmentChangedListener() {
        return environmentChangedListener;
    }
 
    public void setEnvironmentChangedListener(EnvironmentChangedListener environmentChangedListener) {
        this.environmentChangedListener = environmentChangedListener;
    }
 
    @Override
    public void onSensorChanged(SensorEvent event) {
        float luxLevel = event.values[0];
        luxLevel = lowPassFilter.submit(luxLevel);
        Environment oldEnvironment = currentEnvironment;
        if (luxLevel < THRESHOLD_NIGHT_LUX){
            currentEnvironment = Environment.NIGHT;
        } else if (luxLevel > THRESHOLD_DAY_LUX){
            currentEnvironment = Environment.DAY;
        }
        if (hasChanged(oldEnvironment, currentEnvironment)){
            callListener(currentEnvironment);
        }
    }
 
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
 
    private boolean hasChanged(Environment oldEnvironment, Environment newEnvironment) {
        return oldEnvironment != newEnvironment;
    }
 
    private void callListener(Environment environment) {
        if (environmentChangedListener == null || environment == null){
            return;
        }
        switch (environment) {
            case DAY:
                environmentChangedListener.onDayDetected();
                break;
            case NIGHT:
                environmentChangedListener.onNightDetected();
                break;
        }
    }
}
