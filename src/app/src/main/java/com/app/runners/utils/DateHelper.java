package com.app.runners.utils;


import com.app.runners.R;
import com.app.runners.rest.core.ParserUtils;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sergiocirasa on 23/8/17.
 */

public class DateHelper {

    public static boolean isYesterday(Date date){
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTimeZone(TimeZone.getTimeZone("GMT"));
        c2.setTime(date); // your date

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    public static boolean isToday(Date date){
        Calendar c1 = Calendar.getInstance(); // today
        Calendar c2 = Calendar.getInstance();
        c2.setTimeZone(TimeZone.getTimeZone("GMT"));
        c2.setTime(date); // your date

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    public static boolean thisYear(Date date){
        Calendar c1 = Calendar.getInstance(); // today
        Calendar c2 = Calendar.getInstance();
        c2.setTimeZone(TimeZone.getTimeZone("GMT"));
        c2.setTime(date); // your date

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
            return true;
        }
        return false;
    }

    public static String stylizedDate(Date date){
        if(date==null)
            return null;

        if(isYesterday(date)){
            return AppController.getInstance().getString(R.string.yesterday);
        }else if(isToday(date)){
            return AppController.getInstance().getString(R.string.today);
        }else if(!thisYear(date)){
            return ParserUtils.parseDate(date,"dd-MM-yyyy");
        }else{
            String [] months= AppController.getInstance().getResources().getStringArray(R.array.months);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(date);
            String de = AppController.getInstance().getString(R.string.de);
            int month = c2.get(Calendar.MONTH);
            int day = c2.get(Calendar.DAY_OF_MONTH);
            if(month < months.length) {
                FirebaseCrash.log("MES Incorrecto"+ParserUtils.parseDate(date,"dd-MM-yyyy"));
                return "" + day + " " + de + " " + months[month];
            }else{
                return ParserUtils.parseDate(date,"dd-MM-yyyy");
            }
        }
    }
}
