/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

import java.util.ArrayList;

/**
 *
 * @author fernanda
 */
public class Vehicle {
    
    int id; // vehicle ID
    ArrayList<Move> trace; // vehicle trace composed by a set of moves
    double initBattery; // initial battery (a value between 0 and Global.BATTERY_CAPACITY)
    double routeSize; // total size of the vehicle route in meters
    
    Vehicle(int id, double initBattery){
        this.id = id;
        this.routeSize = 0;
        this.initBattery = initBattery;
        this.trace = new ArrayList<>();
    }
    
    int getID(){
        return this.id;
    }
    
    double getInitBattery(){
        return this.initBattery;
    }
    
    void addMove(Move m){
        this.trace.add(m);
        this.routeSize += m.getDistance();
    }
    
    ArrayList<Move> getTrace(){
        return this.trace;
    }
    
    double getRouteSize(){
        return this.routeSize;
    }
    
    void printTrace(){
        System.out.println("Trace for vehicle " + id);
        for (Move m : trace) {
            System.out.println("[" + m.getSquare().getCoordX() + "," + m.getSquare().getCoordY() + "] " + m.getTimeIn() + " " + m.getTimeOut() + " " + m.getDistance());
        }
    }
}
