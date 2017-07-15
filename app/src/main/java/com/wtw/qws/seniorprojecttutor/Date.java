package com.wtw.qws.seniorprojecttutor;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by Chris on 4/26/2017.
 * Helper class designed to provide date related methods such as converting to different formats and comparisons
 */

public class Date {

    private String  currentMonth, currentDay, currentYear, currentDate, formattedTimesArray;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCurrentDate(){
        //getting current date
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.MONTH)+1 <10) {
            currentMonth = "0" + String.valueOf(cal.get(Calendar.MONTH) + 1);
        }else{
            currentMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
        }
        if(cal.get(Calendar.DAY_OF_MONTH) <10){
            currentDay = "0" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }else{
            currentDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }

        currentYear = String.valueOf(cal.get(Calendar.YEAR));
        currentDate = currentMonth + currentDay + currentYear.substring(2,4);
        Log.i("CURRENT_DATE", currentDate);
        return currentDate;
    }

    /**
     *
     * @param newDate
     * @return TRUE if newDate is after the current date (future)
     * @return FALSE if newDate is before the current date (past)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean compareDates(String newDate){
        Calendar cal = Calendar.getInstance();
        int currMonth, currYear, currDay, newMonth, newYear, newDay;
        currMonth = (cal.get(Calendar.MONTH) + 1);
        currYear = Integer.parseInt(String.valueOf(cal.get(Calendar.YEAR)).substring(2,4));
        currDay = (cal.get(Calendar.DAY_OF_MONTH));


        newMonth = Integer.parseInt(newDate.substring(0,2));
        newDay = Integer.parseInt(newDate.substring(2,4));
        newYear = Integer.parseInt(newDate.substring(4,6));

        Log.i("DATES", "Years:"+currYear + " " + newYear + " mons "+currMonth+" "+ newMonth+ " days "+ currDay + ""+ newDay);
        if(newYear == currYear){
            if(newMonth == currMonth){
                if(newDay == currDay){
                    return false;//make an appt 1 day in advance
                }else if(newDay > currDay){
                    return true;
                }else if (newDay < currDay){
                    return false;
                }
            }else if(newMonth > currMonth){
                return true;
            }else if(newMonth < currMonth){
                return false;
            }
        }else if (newYear > currYear){
            return true;
        }else if (newYear < currYear){
            return false;
        }

        return false;

    }


}
