package com.linklab.emmanuelogunjirin.besi_c;

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

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or App modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class BESIWatchFace extends CanvasWatchFaceService
{

    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. Defaults to one second
     * because the watch face needs to update seconds in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
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
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(StartWatchActivity);    // Starts the watch face
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private float mXOffset;
        private float mYOffset;
        private Paint mBackgroundPaint;
        private Paint mTextPaint;
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private boolean mLowBitAmbient;
//        private boolean mBurnInProtection;
        private boolean mAmbient;

        @Override
        public void onCreate(SurfaceHolder holder)
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face

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
//            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
//            startActivity(StartWatchActivity);    // Starts the watch face
            super.onVisibilityChanged(visible);

            if (visible)
            {
                registerReceiver();
                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
            else
            {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver()
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
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
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
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
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = BESIWatchFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            mTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties)
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
//            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick()
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
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

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override

        public void onTapCommand(int tapType, int x, int y, long eventTime)
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            switch (tapType)
            {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds)
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            // Draw the background.
            if (isInAmbientMode())
            {
                canvas.drawColor(Color.BLACK);
            }
            else
            {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            @SuppressLint("DefaultLocale") String text = mAmbient
                    ? String.format("%d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE))
                    : String.format("%d:%02d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND));
            canvas.drawText(text, mXOffset, mYOffset, mTextPaint);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer()
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning())
            {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage()
        {
            Intent StartWatchActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(StartWatchActivity);    // Starts the watch face
            invalidate();
            if (shouldTimerBeRunning())
            {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}