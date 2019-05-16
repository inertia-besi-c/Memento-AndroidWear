package com.linklab.INERTIA.besi_c;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/***
 This system was automatically made by the android studio watch face service. The only thing added was that when a screen is created or some sort of update is called, the system is redirected to start the intent calling main activity. This automatically starts the app and makes sure that the app is automatically launched without need from the user finding the app. This also allows us to safely leave the app and perform functions of the watch without having to fight for dominance and time with the app. Edit at your own risk...
 ***/
@SuppressWarnings("ALL")
public class BESIWatchFace extends CanvasWatchFaceService
{
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);        // Update rate in milliseconds for interactive mode. Defaults to one second.
    private static final int MSG_UPDATE_TIME = 0;   //      * Handler message id for updating the time periodically in interactive mode.
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private String System_Log = Preference.System;     // Gets the sensors from preferences.
    @Override
    public Engine onCreateEngine()
    {
        return new Engine();
    }

    private static class EngineHandler extends Handler
    {
        private final WeakReference<BESIWatchFace.Engine> mWeakReference;

        EngineHandler(BESIWatchFace.Engine reference)
        {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg)
        {
            BESIWatchFace.Engine engine = mWeakReference.get();
            if (engine != null)
            {
                switch (msg.what)
                {
                    case MSG_UPDATE_TIME: engine.handleUpdateTimeMessage();
                    break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine
    {

        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Paint mBackgroundPaint;
        private Paint mTextPaint;
        private boolean mRegisteredTimeZoneReceiver = false;
        private float mXOffset;
        private float mYOffset;
        private boolean mLowBitAmbient;
        private boolean mAmbient;
        private Calendar mCalendar;

        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder)
        {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(BESIWatchFace.this).setAcceptsTapEvents(true).build());
            mCalendar = Calendar.getInstance();
            Resources resources = BESIWatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            // Initializes background.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

            // Initializes Watch Face.
            mTextPaint = new Paint();
            mTextPaint.setTypeface(NORMAL_TYPEFACE);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
        }

        @Override
        public void onDestroy()
        {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            super.onVisibilityChanged(visible);

            if (visible)
            {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());         // Update time zone in case it changed while we weren't visible.
                invalidate();
            }
            else
            {
                unregisterReceiver();
            }
            updateTimer();
        }

        private void registerReceiver()
        {
            if (mRegisteredTimeZoneReceiver)
            {
                return;
            }

            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            BESIWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver()
        {
            if (!mRegisteredTimeZoneReceiver)
            {
                return;
            }

            mRegisteredTimeZoneReceiver = false;
            BESIWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets)
        {
            super.onApplyWindowInsets(insets);
            Resources resources = BESIWatchFace.this.getResources();            // Load resources that have alternate values for round watches.
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            mTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties)
        {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick()
        {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode)
        {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            if (mLowBitAmbient)
            {
                mTextPaint.setAntiAlias(!inAmbientMode);
            }
            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime)
        {
            String data =  ("BESI Watchface," + "Screen Tapped at," + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
            DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, System_Log, data);      // Logs it into a file called System Activity.
            datalog.LogData();      // Saves the data into the directory.

            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            switch (tapType)
            {
                case TAP_TYPE_TOUCH:                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:                    // The user has completed the tap gesture.
                    startActivity(StartWatchActivity);
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds)
        {
            if (isInAmbientMode())
            {
                canvas.drawColor(Color.BLACK);
            }
            else
            {
                Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(StartWatchActivity);    // Starts the watch face
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            @SuppressLint("DefaultLocale") String text = mAmbient
                    ? String.format("%d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE))
                    : String.format("%d:%02d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND));
            canvas.drawText(text, mXOffset, mYOffset, mTextPaint);
        }

        private void updateTimer()
        {

            if (!isInAmbientMode())
            {
                Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(StartWatchActivity);    // Starts the watch face
            }

            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);

            if (shouldTimerBeRunning())
            {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning()
        {
            return isVisible() && !isInAmbientMode();
        }

        private void handleUpdateTimeMessage()
        {
            if (!isInAmbientMode())
            {
                Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(StartWatchActivity);    // Starts the watch face
            }

            if (shouldTimerBeRunning())
            {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
            invalidate();
        }
    }
}
