package com.linklab.INERTIA.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


// FollowUp EMA

public class FollowUpEMA extends WearableActivity       // This is the main activity for the questions
{
    private PowerManager.WakeLock wakeLock;
    private Button res, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private int resTaps = 0;
    private ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses;
    private int[] UserResponseIndex;
    public Vibrator v;      // The vibrator that provides haptic feedback.

    private Timer EMARemindertimer;
    private int EMAReminderDelay = new Preferences().PainEMAReminderDelay;
    private long EMAReminderInterval = new Preferences().PainEMAReminderInterval; //Time before pinging user after not finishing EMA
    private int ReminderNumber = new Preferences().FollowUpEMAReminderNumber;
    private int ReminderCount = 0;
    private int CurrentQuestion = 0;

    private String[] Questions;
    private String[][] Answers;

    private String[] CaregiverQuestions =
            {
                    "Is the patient still having cancer pain now?",
                    "What is the patient's pain level?",
                    "How distressed are you?",
                    "How distressed is the patient?",
                    "Did the patient take an additional opioid for the pain?"
            };
    private String[][] CaregiverAnswers =
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very", "Unsure"},
                    {"Yes", "No"}
            };

    private String[] PatientQuestions =
            {
                    "Are you still having cancer pain now??",
                    "What is your pain level?",
                    "How distressed are you?",
                    "How distressed is your caregiver?",
                    "Did you take an additional opioid for the pain?"
            };
    private String[][] PatientAnswers =
            {
                    {"Yes", "No"},
                    {"1","2","3","4","5","6","7","8","9","10"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very", "Unsure"},
                    {"Yes", "No"}
            };

    @Override

    // When the screen is created, this is run.
    protected void onCreate(Bundle savedInstanceState)
    {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Follow Up EMA:wakeLock");
        wakeLock.acquire((1+ReminderNumber)*EMAReminderInterval+5000);
        /* Vibrator values and their corresponding requirements */
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ema);

        /* Buttons on the EMA screen that were added */
        back = findViewById(R.id.Back);
        next = findViewById(R.id.Next);
        req = findViewById(R.id.EMA_req);
        res = findViewById(R.id.EMA_res);

        if (new Preferences().Role.equals("PT"))
        {
            Questions = PatientQuestions;
            Answers = PatientAnswers;
        }
        else
        {
            Questions = CaregiverQuestions;
            Answers = CaregiverAnswers;
        }

        /* This is the haptic feedback feel that is done when the EMA buttons are pressed. */
        res.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                resTaps+=1;
                Cycle_Responses();
            }
        });

        UserResponses = new String[Questions.length];
        UserResponseIndex = new int[UserResponses.length];
        //q1();       // Moves on to question 1


        EMARemindertimer = new Timer();
        EMARemindertimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Log.i("EMAR","Running EMAReminder");
                Log.i("EMAR",String.valueOf(ReminderCount<=ReminderNumber));
                if (ReminderCount <= ReminderNumber)
                {
                    Log.i("EMAR","Vibrating...");
                    v.vibrate(600);
                    ReminderCount ++;
                }
                else
                {
                    Log.i("EMAR","Stopping Timer");
                    Submit();
                }
            }
        },EMAReminderDelay,EMAReminderInterval);

        QuestionSystem();

        // Enables Always-on
        setAmbientEnabled();
    }


    private int Cycle_Responses()
    {
        int index = resTaps%responses.size();
        res.setText(responses.get(index));
        return index;
    }

    private void LogActivity()
    {
        String data =  (new Utils().getTime()) + ",EMA_Followup," + String.valueOf(CurrentQuestion) + "," + UserResponses[CurrentQuestion];
        DataLogger datalog = new DataLogger("Followup_EMA_Activity.csv",data);
        datalog.LogData();
    }


    @SuppressLint("SetTextI18n")
    private void QuestionSystem()
    {
        if (CurrentQuestion == 0){back.setBackgroundColor(getColor(R.color.grey));}
        else {back.setBackgroundColor(getColor(R.color.dark_red));}

        if (CurrentQuestion == Questions.length-1){next.setText("Submit");}
        else {next.setText("Next");}

        if (CurrentQuestion < Questions.length)
        {
            resTaps = UserResponseIndex[CurrentQuestion];
            req.setText(Questions[CurrentQuestion]);
            responses.clear();
            Collections.addAll(responses, Answers[CurrentQuestion]);
            Cycle_Responses();

            // Waits for the next button to be clicked.
            next.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View view)      // Haptic Feedback
                {
                    v.vibrate(20);
                    UserResponses[CurrentQuestion] = res.getText().toString();
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();
                    LogActivity();
                    if (UserResponses[0].equals("Yes"))     // If the answer to is "yes", moves on to question 2
                    {
                        CurrentQuestion++;
                        QuestionSystem();
                    }
                    else
                    {
                        Cancel();    // Else, it closes the question screen.
                    }
                }
            });

            // If the back button is clicked
            back.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    v.vibrate(20); // Haptic Feedback
                    UserResponses[CurrentQuestion] = res.getText().toString();
                    UserResponseIndex[CurrentQuestion] = Cycle_Responses();
                    LogActivity();
                    if (CurrentQuestion == 0)
                    {
                        //Cancel();
                    }
                    else
                    {
                        CurrentQuestion --;
                        QuestionSystem();
                    }
                }
            });
        }
        else
        {
            Submit();
        }
    }

    private void ThankYou()
    {
        EMARemindertimer.cancel();
        Context context = getApplicationContext();
        CharSequence text = "Thank You!";       // Pop up information to the person
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();
        finish();
    }

    /* This is the end of survey part. It submits the data. */
    private void Submit()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);     // A date variable is initialized
        Date date = new Date();
        StringBuilder log = new StringBuilder(dateFormat.format(date));     // Starts to log the data

        for (String UserResponse : UserResponses)
        {
            log.append(",").append(UserResponse);       // Any string that has data, get it.
        }

        /* Logs the data in a csv format */
        DataLogger dataLogger = new DataLogger("Followup_EMA_Results.csv", log.toString());
        dataLogger.LogData();


        ThankYou();

    }

    @Override
    public void onDestroy()
    {
        wakeLock.release();
        EMARemindertimer.cancel();
        super.onDestroy();
    }

    private void Cancel()
    {
        ThankYou();
        finish();   // Closes the entire survey
    }
}
