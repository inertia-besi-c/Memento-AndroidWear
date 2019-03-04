package com.linklab.emmanuelogunjirin.besi_c;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EMA extends WearableActivity
{
    private int qcount = 5;     // This is the amount of questions to be shown.
    private Button res, back, next;
    private TextView req;
    private int resTaps = 0;
    private ArrayList<String> responses = new ArrayList<>();
    private String[] UserResponses = new String[qcount];
    public Vibrator v;


    @Override
    // When the screen is created, this is run.
    protected void onCreate(Bundle savedInstanceState)
    {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        v.vibrate(300);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ema);

        back = findViewById(R.id.Back);
        next = findViewById(R.id.Next);
        req = findViewById(R.id.EMA_req);

        res = findViewById(R.id.EMA_res);

        res.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
            {
                v.vibrate(20);
                resTaps+=1;
                Cycle_Responses();
            }
        });

        q1();

        // Enables Always-on
        setAmbientEnabled();
    }


    private void Cycle_Responses()
    {
        res.setText(responses.get(resTaps%responses.size()));
    }

    // This is Question 1
    @SuppressLint("SetTextI18n")
    private void q1()
    {
        back.setText("Cancel");
        resTaps = 0;
        String question = "Is patient having pain now?";
        responses.clear();
        responses.add("Yes");
        responses.add("No");

        req.setText(question);
        Cycle_Responses();
        // If the next button is clicked
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View view)
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
                v.vibrate(20);
                Cancel();
            }
        });
    }

    // This is Question 2
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

    // This is Question 3
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

    // This is Question 4
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

    // This is Question 5
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

    private void Submit()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date();
        StringBuilder log = new StringBuilder(dateFormat.format(date));

        for (String UserResponse : UserResponses)
        {
            log.append(",").append(UserResponse);
        }

        DataLogger dataLogger = new DataLogger("EMA_Results.csv", log.toString());
        dataLogger.LogData();
        Context context = getApplicationContext();
        CharSequence text = "Thank You!";       // Pop up information to the person

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        finish();
    }

    private void Cancel()
    {
        finish();   // Closes the entire survey
    }
}

