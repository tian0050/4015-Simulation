

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TIAN MAOKUN
 */
public class CallEvent{
    public int callID;
    public double time;
    public double duration;
    public int station;
    public String type;
    
    public CallEvent(int callID, int station, double time, double duration, String type){
        this.callID = callID;
        this.station = station;
        this.time = time;
        this.type = type;
        this.duration = duration;
    }
}
