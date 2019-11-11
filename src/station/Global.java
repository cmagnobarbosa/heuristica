/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

/**
 *
 * @author fernanda
 */
public class Global {
    
    public final static int GRID_N = 100; // GRID N x N
    public final static double BATTERY_CAPACITY = 30000; // capacity of vehicle battery (autonomy in km)
    public static double DEPLETION_RATE; // depletion rate per kilometer
    
    public static void setDepletionRate(double depletion){
        DEPLETION_RATE = depletion;
    }
}
