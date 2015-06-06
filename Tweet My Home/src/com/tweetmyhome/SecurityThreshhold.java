package com.tweetmyhome;

import static com.esotericsoftware.minlog.Log.*;
/**
 *
 * @author Klaw Strife
 */
public class SecurityThreshhold {


    public final static int NOT_DEFINED = -1;
    public final static int PRE_BREAK_FUNCTION_REFLEX_VALUE = 3;
    public final static int POST_BREAK_FUNCTION_REFLEX_VALUE = 1;
    public final static long ESTIMULATION_STEP_VALUE =2; // 2 THRESHOLD
    public final static long ESTIMULATION_DOWN_TIME_MILLIS = 1000 * 60 * 3; //2 MINUTES

    private final static double m = (double) ESTIMULATION_STEP_VALUE / ESTIMULATION_DOWN_TIME_MILLIS;
    
    private long functionBreakTimeMillis;
    private double securityEstimulationValue;
    private long lastEstimulationMillis;
    private final Security sec;
    
    public enum SecurityRisk{
        NONE,LOW,MEDIUM,HIGH,HYPER_HIGH,ULTRA_MEGA_HIGH
    }

    /**
     * functionBreakTimeMillis set -> 4 min in milliseconds
     *
     * @param sec
     */
    public SecurityThreshhold(Security sec) {
        this.sec = sec;
        functionBreakTimeMillis = 2 * 60 * 1000; // 2 minutes
        lastEstimulationMillis = 0;
    }

    public long getFunctionBreakTimeMillis() {
        return functionBreakTimeMillis;
    }

    /**
     * Este parametro permite definir ciertos
     * @param functionBreakTimeMillis 
     */
    public void setFunctionBreakTimeMillis(long functionBreakTimeMillis) {
        this.functionBreakTimeMillis = functionBreakTimeMillis;
    }
    
    public boolean isEstimulationOverThreshold() {
        int y = getReflexFunctionVsTime();
        if (y != NOT_DEFINED) {
            if (getEstimulation() >= y) {
                return true;
            }
        }
        return false;
    }

    public void estimulate() {
        long actualMillis = System.currentTimeMillis();
        long estimulationDiffMillis = actualMillis - lastEstimulationMillis;
        if (lastEstimulationMillis == 0) { // primera vez estimulado
            securityEstimulationValue = ESTIMULATION_STEP_VALUE;
        } else {
            double y = -m * estimulationDiffMillis;
            securityEstimulationValue += y + ESTIMULATION_STEP_VALUE;
            if (securityEstimulationValue < 0) {
                securityEstimulationValue = 0;
            }
        }
        lastEstimulationMillis = actualMillis;
    }

    public double getEstimulation() {
        long actualMillis = System.currentTimeMillis();
        long estimulationDiffMillis = actualMillis - lastEstimulationMillis;
        double r = -m * estimulationDiffMillis + securityEstimulationValue;
        return r < 0 ? 0 : r; 
    }
    
    public boolean isPreBreakFunctionOn(){
        return getReflexFunctionVsTime() == PRE_BREAK_FUNCTION_REFLEX_VALUE;
    }
    
    public SecurityRisk getRiskByEstimulation(){
        if(isEstimulationBetween(0,1))
            return SecurityRisk.NONE;
        else if(isEstimulationBetween(1,4))
            return SecurityRisk.LOW;
        else if(isEstimulationBetween(4,10))
            return SecurityRisk.MEDIUM;
        else if(isEstimulationBetween(10,20))
            return SecurityRisk.HIGH;
        else if(isEstimulationBetween(20,35))
            return SecurityRisk.HYPER_HIGH;
        else if(getEstimulation() >= 35)
            return SecurityRisk.ULTRA_MEGA_HIGH;
        return SecurityRisk.NONE;
    }
    
    private boolean isEstimulationBetween(double min,double max){
        double e = getEstimulation();
        return e >= min && e < max;
    }
    
    private int getReflexFunctionVsTime() {
        if (sec.isEnabled()) {
            if (sec.getEnabledMillisTime() > functionBreakTimeMillis) {
                return POST_BREAK_FUNCTION_REFLEX_VALUE;
            }else{
                return PRE_BREAK_FUNCTION_REFLEX_VALUE;
            }
        }
        return NOT_DEFINED;
    }

    @Override
    public String toString() {
        return "SecurityThreshhold{isPreBreakFunctionOn=" + isPreBreakFunctionOn() + "," 
                + "SecurityRisk=" + getRiskByEstimulation()
                + ", isEstimulationOverThreshold() =" + isEstimulationOverThreshold()
                + ", estimulation=" + getEstimulation() + '}';
    }

}
