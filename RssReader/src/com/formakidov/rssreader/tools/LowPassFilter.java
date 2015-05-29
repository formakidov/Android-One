package com.formakidov.rssreader.tools;

public class LowPassFilter {
	 
    private float filteredValue;
    private final float smoothing;
    private boolean firstTime = true;
 
    public LowPassFilter(float smoothing) {
        this.smoothing = smoothing;
    }
 
    public float submit(float newValue){
        if (firstTime){
            filteredValue = newValue;
            firstTime = false;
            return filteredValue;
        }
        filteredValue += (newValue - filteredValue) / smoothing;
        return filteredValue;
    }
}
