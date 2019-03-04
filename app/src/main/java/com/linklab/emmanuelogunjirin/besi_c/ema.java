package com.linklab.emmanuelogunjirin.besi_c;

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

public class ema extends WearableActivity {

    private int qcount = 5;
    private Button res,back,next;
    private TextView req;

    private int resTaps = 0;
    private ArrayList<String> responses = new ArrayList<String>();

    private String[] UserResponses = new String[qcount];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ema);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);


        back = (Button) findViewById(R.id.Back);
        next = (Button) findViewById(R.id.Next);

        req = (TextView) findViewById(R.id.ema_req);

        res = (Button) findViewById(R.id.ema_res);
        res.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
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
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[0] = res.getText().toString();
                if (UserResponses[0] == "Yes")
                    q2();
                else
                    Cancel();
            }
        });
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Cancel();
            }
        });
    }

    private void q2()
    {
        back.setText("Back");
        resTaps = 0;
        String question = "What is patient's pain level?";
        responses.clear();
        for (int i = 1; i<=10;i++)
        {
            responses.add(""+i);
        }

        req.setText(question);
        Cycle_Responses();
        back.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[1] = res.getText().toString();
                q1();
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[1] = res.getText().toString();
                q3();
            }
        });
    }

    private void q3()
    {
        resTaps = 0;
        String question = "How distressed are you?";
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
            public void onClick(View v)
            {
                UserResponses[2] = res.getText().toString();
                q2();
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[2] = res.getText().toString();
                q4();
            }
        });
    }
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
            public void onClick(View v)
            {
                UserResponses[3] = res.getText().toString();
                q3();
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[3] = res.getText().toString();
                q5();
            }
        });
    }
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
            public void onClick(View v)
            {
                UserResponses[4] = res.getText().toString();
                q3();
            }
        });
        next.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v)
            {
                UserResponses[4] = res.getText().toString();
                Submit();
            }
        });
    }

    private void Submit()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String log = dateFormat.format(date);

        for (int i = 0; i < UserResponses.length; i++)
        {
            log+=","+UserResponses[i];
        }

        DataLogger dataLogger = new DataLogger("EMA_Results.csv",log);

        dataLogger.LogData();
        Context context = getApplicationContext();
        CharSequence text = "Thank You!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        finish();
    }
    private void Cancel()
    {
        finish();
    }
}

