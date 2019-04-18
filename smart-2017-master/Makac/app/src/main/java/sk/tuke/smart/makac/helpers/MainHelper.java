package sk.tuke.smart.makac.helpers;

/**
 * Created by tinak on 10/29/2017.
 */

public final class MainHelper {
    /* constants */
    private static final float MpS_TO_MIpH = 2.23694f;
    private static final float KM_TO_MI = 0.62137119223734f;
    private static final float MINpKM_TO_MINpMI = 1.609344f;


    /**
     * return string of time in format HH:MM:SS
     */
    public static String formatDuration(long time){
        String seconds, minutes, hours, ftime;

        long sec = (time/1000) % 60;
        long min = ((time/1000)/60) % 60;
        long hr = (((time/1000)/60)/60) % 24;


        hours=String.valueOf(hr);
        if(hr==0){
            hours="00";
        }
        if(hr <10 && hr > 0){
            hours = "0"+hours;
        }

        minutes=String.valueOf(min);
        if(min ==0){
            minutes="00";
        }
        if(min <10 && min > 0){
            minutes = "0"+minutes;
        }

        seconds=String.valueOf(sec);
        if(sec == 0){
            seconds = "00";
        }

        if(sec <10 && sec > 0){
            seconds = "0"+seconds;
        }

        ftime = hours+ ":" +minutes+ ":" +seconds;
        if(hr == 0 && sec == 0 && min ==0){
            return "00:00:00";
        }
        return ftime;
    }

    /**
     * convert m to km and round to 2 decimal places and return as string
     */
    public static String formatDistance(double n){
        String km;
        double kilometers = Math.round((n/1000)*100)/100.00;
        km = String.valueOf(kilometers);
        return km;
    }

    /**
     * round number to 2 decimal places and return as string
     */
    public static String formatPace(double n){
        double pace = Math.round(n*100)/100.00;

        return String.valueOf(pace);

    }

    /**
     * round number to integer
     */
    public static String formatCalories(double n){
        int calories = (int) Math.round(n);

        return String.valueOf(calories);
    }

    /* convert km to mi (multiply with corresponding constant) */
    public static double kmToMi(double n){
        return n*KM_TO_MI;
    }

    /* convert m/s to mi/h (multiply with corresponding constant) */
    public static double mpsToMiph(double n){
        return n*MpS_TO_MIpH;
    }

    /* convert min/km to min/mi (multiply with corresponding constant) */
    public static double minpkmToMinpmi(double n){
        return n*MINpKM_TO_MINpMI;
    }


}
