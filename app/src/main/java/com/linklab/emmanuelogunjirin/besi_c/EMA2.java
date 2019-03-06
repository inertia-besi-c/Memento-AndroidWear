package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EMA2 extends WearableActivity       // This is the second main activity for the questions
{
    private int qcount = 5;     // This is the amount of questions to be shown.
    private Button res, back, next;     // These are the buttons shown on the screen to navigate the watch
    private TextView req;   // This is a text view for the question
    private int resTaps = 0;
    private ArrayList<String> responses = new ArrayList<>();    // This is a string that is appended to.
    private String[] UserResponses = new String[qcount];
    public Vibrator v;      // The vibrator that provides haptic feedback.


    @Override

    // When the screen is created, this is run.
    protected void onCreate(Bundle savedInstanceState)
    {
        /* Vibrator values and their corresponding requirements */
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ema);

        /* Buttons on the EMA screen that were added */
        back = findViewById(R.id.Back);
        next = findViewById(R.id.Next);
        req = findViewById(R.id.EMA_req);
        res = findViewById(R.id.EMA_res);

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

        q1();       // Moves on to question 1

        // Enables Always-on
        setAmbientEnabled();
    }


    private void Cycle_Responses()
    {
        res.setText(responses.get(resTaps%responses.size()));
    }

    // This is Question 1
    @SuppressLint("SetTextI18n")
    /* The survey begins with Question 1 here */
    private void q1()
    {
        back.setText("Cancel");
        resTaps = 0;
        String question = "Is patient having cancer pain now?";
        responses.clear();
        responses.add("Yes");
        responses.add("No");

        req.setText(question);
        Cycle_Responses();
        // If the next button is clicked
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)      // Haptic Feedback
            {
                v.vibrate(20);
                UserResponses[0] = res.getText().toString();
                if (UserResponses[0].equals("Yes"))     // If the answer to is "yes", moves on to question 2
                {
                    q2();
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
                v.vibrate(20);      // Haptic Feedback
                Cancel();
            }
        });
    }

    // This is Question 2 (See question one for other comments, they follow similar structure)
    @SuppressLint("SetTextI18n")
    private void q2()
    {
        back.setText("Back");
        resTaps = 0;
        String question = "What is patient's pain level?";
        responses.clear();
        for (int i=1; i<=10; i++)
        {
            responses.add(""+i);
        }
        req.setText(question);
        Cycle_Responses();
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[1] = res.getText().toString();
                q1();   // Goes back to question 1
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[1] = res.getText().toString();
                q3();   // Goes back to question 3
            }
        });
    }

    // This is Question 3 (See question one for other comments, they follow similar structure)
    private void q3()
    {
        resTaps = 0;
        String question = "How distressed are you?";
        responses.clear();

        // Response Options
        responses.add("Not at all");
        responses.add("A little");
        responses.add("Moderately");
        responses.add("Very");

        req.setText(question);
        Cycle_Responses();
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[2] = res.getText().toString();
                q2();   // Goes back to question 2
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[2] = res.getText().toString();
                q4();   // Goes on to question 4
            }
        });
    }

    // This is Question 4 (See question one for other comments, they follow similar structure)
    private void q4()
    {
        resTaps = 0;
        String question = "How distressed is the patient?";
        responses.clear();

        //Response Options
        responses.add("Not at all");
        responses.add("A little");
        responses.add("Moderately");
        responses.add("Very");

        req.setText(question);
        Cycle_Responses();
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[3] = res.getText().toString();
                q3();   // Goes back to question 3
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[3] = res.getText().toString();
                q5();   // Goes on to question 4
            }
        });
    }

    // This is Question 5 (See question one for other comments, they follow similar structure)
    private void q5()
    {
        resTaps = 0;
        String question = "Did patient take an opioid for the pain?";
        responses.clear();

        responses.add("Yes");
        responses.add("No");
        responses.add("Unsure");

        req.setText(question);
        Cycle_Responses();
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[4] = res.getText().toString();
                q4();   // Goes back to question 4
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                UserResponses[4] = res.getText().toString();
                Submit();   // Submits the responses.
            }
        });
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
        DataLogger dataLogger = new DataLogger("EMA2_Results.csv", log.toString());
        dataLogger.LogData();
        Context context = getApplicationContext();
        CharSequence text = "Thank You!";       // Pop up information to the person

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();
        finish();
    }

    private void Cancel()
    {
        finish();   // Closes the entire survey
    }
}

