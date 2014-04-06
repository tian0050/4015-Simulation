
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author TIAN MAOKUN
 */
public class RandomVariableGenerator {
    
    public static double generateExpDist(double beta){
        //probability density function f(x) = (1/beta)*e^(-x/beta)
        return -beta*Math.log(1-Math.random());
    }
    
    public static double generateExpDistPlus10(double beta){
        //probability density function f(x) = (1/beta)*e^(-x/beta)
        return -beta*Math.log(1-Math.random()) + 10;
    }
    
    public static int generateDiscreteUniformDist(int[] slots){
        return ((int)(slots.length*Math.random()))+1;
    }
    
    public static double generateContinuousUniformDist(double min, double max){
        return (max-min)*Math.random()+min;
    }
    
    public static double generateNormDist(double mean, double deviation){
        return new Random().nextGaussian()*deviation+mean;
    }

}
