package main;

/**
 * Created by OBNinja on 8/31/14.
 */
public class PIDController {

    /*working variables*/
    long lastTime;
    double Input, Output, Setpoint;
    double errSum, lastErr;
    double kp, ki, kd;
    double maxOut, minOut, scaleFactor, shiftFactor;

    public PIDController(double akp, double aki, double akd, double amaxOut, double aminOut, double ascaleFactor, double ashiftFactor){
        kp = akp;
        kd = akd;
        ki = aki;
        maxOut = amaxOut;
        minOut = aminOut;
        scaleFactor = ascaleFactor;
        shiftFactor = ashiftFactor;
    }

    public double Compute()
    {
       /*How long since we last calculated*/
            long now = System.currentTimeMillis();
            double timeChange = (double)(now - lastTime);

       /*Compute all the working error variables*/
            double error = Setpoint - Input;
            errSum += (error * timeChange);
            double dErr = (error - lastErr) / timeChange;

       /*Compute PID Output*/
            Output = kp * error + ki * errSum + kd * dErr;

       /*Remember some variables for next time*/
            lastErr = error;
            lastTime = now;
        Output = (Output * scaleFactor) + shiftFactor;
        if ((Output < minOut) && (Output > maxOut)){
            Output = 0;
        }
        return Output;
    }

    public void SetTunings(double Kp, double Ki, double Kd)
    {
        kp = Kp;
        ki = Ki;
        kd = Kd;
    }
}
