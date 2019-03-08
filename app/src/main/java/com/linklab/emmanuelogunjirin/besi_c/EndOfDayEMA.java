
package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


public class EndOfDayEMA extends WearableActivity       // This is the main activity for the questions
{
    private Button res, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private int resTaps = 0;
    private ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses;
    private int[] UserResponseIndex;
    public Vibrator v;      // The vibrator that provides haptic feedback.

    private Timer EMARemindertimer;
    private int EMAReminderDelay = new Preferences().PainEMAReminderDelay;
    private int EMAReminderInterval = new Preferences().PainEMAReminderInterval; //Time before pinging user after not finishing EMA
    private int ReminderNumber = new Preferences().PainEMAReminderNumber;
    private int ReminderCount = 0;
    private int CurrentQuestion = 0;

    private String [] Questions;
    private String[][] Answers;

    private String[] CaregiverQuestions =
            {
                    "How active were you?",
                    "How busy was your home?",
                    "Time spend outside your home?",
                    "How much time did you spend with other people?",
                    "How distressed were you overall?",
                    "How did the patient's pain interfere with your life?",
                    "How was your mood overall?",
                    "How distressed was the patient overall?",
                    "How would you rate your sleep quality?",
            };
    private String[][] CaregiverAnswers =
            {
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Excellent"},
                    {"None", "A little", "Moderately", "Very", "Unsure"},
                    {"Poor", "Fair", "Good", "Excellent"},
            };

    private String[] PatientQuestions =
            {
                    "How active were you?",
                    "How busy was your home?",
                    "Time spend outside your home?",
                    "How much time did you spend with other people?",
                    "How distressed were you overall?",
                    "How much did pain interfere with your life?",
                    "How was your mood overall?",
                    "How distressed was your caregiver overall?",
                    "How would you rate your sleep quality?",
            };
    private String[][] PatientAnswers =
            {
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Not at all", "A little", "Moderately", "Very"},
                    {"None", "A little", "Medium", "A lot"},
                    {"Poor", "Fair", "Good", "Excellent"},
                    {"None", "A little", "Moderately", "Very", "Unsure"},
                    {"Poor", "Fair", "Good", "Excellent"},
            };

    @Override

    // When the screen is created, this is run.
    protected void onCreate(Bundle savedInstanceState)
    {
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
        String data =  (new Utils().getTime()) + ",EMA_EndOfDay," + String.valueOf(CurrentQuestion) + "," + UserResponses[CurrentQuestion];
        DataLogger datalog = new DataLogger("EndOfDay_EMA_Activity.csv",data);
        datalog.LogData();
    }

    private void QuestionSystem()
    {
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

                    CurrentQuestion++;
                    QuestionSystem();

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
                        Cancel();
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
        DataLogger dataLogger = new DataLogger("EndOfDay_EMA_Results.csv", log.toString());
        dataLogger.LogData();

        EMARemindertimer.cancel();

        ThankYou();

    }

    private void Cancel()
    {
        ThankYou();
        finish();   // Closes the entire survey
    }
}
