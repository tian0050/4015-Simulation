

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TIAN MAOKUN
 */
public class Call{
    
    public int id;
    public double interArrivalTime;
    public int baseStation;
    public double callDuration;
    public double velocity;
    public double distanceToEnd;
    
    public Call(int id){
        this.id = id;
        interArrivalTime = RandomVariableGenerator.generateExpDist(InputSettings.betaForInterArrivalTimeDist);
        baseStation = RandomVariableGenerator.generateDiscreteUniformDist(InputSettings.baseStations);
        callDuration = RandomVariableGenerator.generateExpDistPlus10(InputSettings.betaForCallDurationDist);
        velocity = RandomVariableGenerator.generateNormDist(InputSettings.meanForVelocity, InputSettings.deviationForVelocity)/3600;
        distanceToEnd = RandomVariableGenerator.generateContinuousUniformDist(0, InputSettings.baseLength);
    }
}
