package com.chaoyu.jongwn.taximeter;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by JongWN on 12/23/2017.
 */

public class ShakeEventListener implements SensorEventListener {

    /** Minimum movement force to consider. */
    private static final int MIN_FORCE = 10;

    /**
     * Minimum times in a shake gesture that the direction of movement needs to
     * change.
     */
    private static final int MIN_DIRECTION_CHANGE = 3;

    /** Maximum pause between movements. */
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

    /** Maximum allowed time for shake gesture. */
    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;

    /** Time when the gesture started. */
    private long mFirstDirectionChangeTime = 0;

    /** Time when the last movement started. */
    private long mLastDirectionChangeTime;

    /** How many movements are considered so far. */
    private int mDirectionChangeCount = 0;

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

    /** OnShakeListener that is called when shake is detected. */
    private OnShakeListener mShakeListener;

    private SensorManager mSensorManager;
    private int iAccelReadings, iAccelSignificantReadings;
    private long iAccelTimestamp;
    private  boolean prev = true;
    /**
     * Interface for shake gesture.
     */
    public interface OnShakeListener {

        /**
         * Called when shake gesture is detected.
         */
        void onShake();
        void onStop();
    }



    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double accel, x, y, z;
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        iAccelReadings++;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        accel = Math.abs(
                Math.sqrt(
                        Math.pow(x,2)
                                +
                                Math.pow(y,2)
                                +
                                Math.pow(z,2)
                )
        );
        if (accel > (9.8 + 0.30) || accel < (9.8 -0.30)) {
            iAccelSignificantReadings++;
        }

        //Log.d("When", String.format("event: %f %f %f %f %f", x, y, z, accel, 0.600));

        // Get readings for 2 seconds
        if ( (System.currentTimeMillis() - iAccelTimestamp) < 2000) return;

        // Appeared to be moving 30% of the time?
        // If the bar is this low, why not report motion at the first significant reading and be done with it?
        if (((1.0*iAccelSignificantReadings) / iAccelReadings) > 0.30) {
            mShakeListener.onShake();
            prev = true;
            resetShakeParameters();


        } else {
            if(prev){
                prev = false;
                return;
            }
            mShakeListener.onStop();

            resetShakeParameters();
        }
        iAccelTimestamp = System.currentTimeMillis();
        iAccelReadings = 0;
        iAccelSignificantReadings = 0;
    }

    /**
     * Resets the shake parameters to their default values.
     */
    private void resetShakeParameters() {
        mFirstDirectionChangeTime = 0;
        mDirectionChangeCount = 0;
        mLastDirectionChangeTime = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
