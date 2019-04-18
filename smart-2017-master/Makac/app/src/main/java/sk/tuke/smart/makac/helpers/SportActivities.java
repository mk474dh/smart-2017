package sk.tuke.smart.makac.helpers;

import java.util.List;

import static sk.tuke.smart.makac.helpers.MainHelper.mpsToMiph;

/**
 * Created by tinak on 10/29/2017.
 */

public final class SportActivities {
    /*For counting calories use following formula:
    Kcal ≈ METs ∗ bodyMassKg ∗ timePerformingHours
    As default use 80kg weight.
    From speed list compute avg. speed and use it for method getMET.
    In method getMET use Math.ceil() to get right METs from upper tables.
    If for particular speed value doesn’t exist METs value, use avg METs values
    for calculation from following table and multiply METs with the necessary speed:
    */
    static double[][] runMet = new double[][]{
            { 4, 6.0 },
            { 5, 8.3 },
            { 6, 9.8 },
            { 7, 11.0 },
            { 8, 11.8 },
            { 9, 12.8 },
            { 10, 14.5 },
            { 11, 16.0 },
            { 12, 19.0 },
            { 13, 19.8 },
            { 14, 23.0 }
    };
    static double[][] walkMet = new double[][]{
            { 1, 2.0 },
            { 2, 2.8 },
            { 3, 3.1 },
            { 4, 3.5 }
    };
    static double[][] cyclingMet = new double[][]{
            { 10, 6.8 },
            { 12, 8.0 },
            { 14, 10.0 },
            { 16, 12.8 },
            { 18, 13.6 },
            { 20, 15.8 }
    };
    static double[][] caloriesSpeed = new double[][]{
            { 0, 1.535353535 },
            { 1, 1.14 },
            { 2, 0.744444444 }
    };

    private static double calculateMet(int activity){
        for (double[] aCaloriesSpeed : caloriesSpeed) {
            if (aCaloriesSpeed[0] == activity) {
                System.out.println("Calories: " + aCaloriesSpeed[1]);
                return aCaloriesSpeed[1];
            }
        }
        return 0;
    }

    /**
     * Returns precomputed calories burn for specific sport activity according to 1 MET.
     * @param activityType - sport activity type (0 - running, 1 - walking, 2 - cycling)
     * @param speed - speed in m/s
     * @return
     */
    public static double getMET(int activityType, Float speed){
        double mphSpeed = Math.ceil(mpsToMiph((double)speed));

        switch(activityType) {
            case 0:
                for (double[] aRunMet : runMet) {
                    if (aRunMet[0] == mphSpeed) {
                        return aRunMet[1];
                    }
                }
                return mphSpeed*calculateMet(activityType);
            case 1:
                for (double[] aWalkMet : walkMet) {
                    if (aWalkMet[0] == mphSpeed) {
                        return aWalkMet[1];
                    }
                }
                return mphSpeed*calculateMet(activityType);
            case 2:
                for (double[] aCyclingMet : cyclingMet) {
                    if (aCyclingMet[0] == mphSpeed) {
                        return aCyclingMet[1];
                    }
                }
                return mphSpeed*calculateMet(activityType);
        }

        return 0;
    }

    /**
     * Returns final calories computed from the data provided (returns value in kcal)
     * @param sportActivity - sport activity type (0 - running, 1 - walking, 2 - cycling)
     * @param weight - weight in kg
     * @param speedList - list of all speed values recorded (unit = m/s)
     * @param timeFillingSpeedListInHours - time of collecting speed list (duration of sport activity from first to last speedPoint in speedList)
     * @return
     */

    public static double countCalories(int sportActivity, float weight, List<Float> speedList, double timeFillingSpeedListInHours){
        float listing = speedList.get(speedList.size()-1) + speedList.get(speedList.size()-2) ;


        float avg = listing / 2;
        float timeToHr = (float)timeFillingSpeedListInHours*3600;
        double met = getMET(sportActivity,avg/timeToHr);
        return (weight*((float)timeFillingSpeedListInHours*(float)met)/1000);
    }

}
