package com.linklab.emmanuelogunjirin.besi_c;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Things that we need in the future and do not want to lose.
public class Utils {
    public Utils()
    {

    }
    public String getTime()
    {
        DateFormat datetimeFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SSS", Locale.US);
        Date current = new Date();
        return datetimeFormat.format(current);
    }
}
