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
public class DLHeuristic {
    
    int stationVar[][];
    int coverage[];
    int numCovVehicles, numStations;
    int NV, GRID;
    ArrayList<Vehicle> vehicles;
    
    public DLHeuristic(int NV, int GRID, ArrayList<Vehicle> vehicles){
    
        stationVar = new int[GRID][GRID];
        coverage = new int[vehicles.size()];
        this.NV = NV;
        this.GRID = GRID;
        this.vehicles = vehicles;
        this.numCovVehicles = 0;
        this.numStations = 0;
    }
    
    public void solve(){
                    
        // Constructive Heuristic
        greedyConstruction();
        
        System.out.println("\n*** Solution ***\n");

        System.out.println("Number of stations:" + numStations);
        System.out.println("Number of recharges:" + getNumRecharges());
        
        // Output
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                if (stationVar[i][j] == 1){
                    System.out.print(i+ ";" + j + ";;");
                }
            }
        }
        System.out.println("\n");
        
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                if (stationVar[i][j] == 1){
                    System.out.print("x");
                }
                else System.out.print(" ");
            }
            System.out.println();
        }
        
    }
    
    private void greedyConstruction(){
        
        int[][] vehicleDensity = new int[GRID][GRID];
        
        // Vehicle density in each cell
        for (Vehicle v : vehicles){ // for each car            
            for (Move m : v.getTrace()){            
                vehicleDensity[m.getSquare().getCoordX()][m.getSquare().getCoordY()] ++;
            }
        }
        
        System.out.println("Number of vehicles per square:\n\n");
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                //if (vehicleDensity[i][j] == 1){
                    //System.out.print(i+ ";" + j + ";;");
                    System.out.printf("%5d ", vehicleDensity[i][j]);
                //}
            }
            System.out.println();
        }
        
        while (numCovVehicles < NV) {
            
            // place highest
            int max = -1, best_i = -1, best_j = -1;
            for (int i = 0; i < GRID; i++) {
                for (int j = 0; j < GRID; j++) {
                    if (vehicleDensity[i][j] > max && stationVar[i][j] == 0) {
                        max = vehicleDensity[i][j];
                        best_i = i;
                        best_j = j;
                    }
                }
            }
            
            // deploy RSU
            stationVar[best_i][best_j] = 1;
            numStations++;
            
            // Update coverage
            updateCoverage();
        }

    }
    
    private void updateCoverage(){
            
        for (Vehicle v : vehicles){
            if (coverage[v.getID()] == 0){
                if (v.getRouteSize() <= v.getInitBattery()){
                    coverage[v.getID()] = 1;
                    numCovVehicles++;
                }
                else if (isCovered(v)){
                    coverage[v.getID()] = 1;
                    numCovVehicles++;
                }
            }            
        }
    }
    
    private boolean isCovered(Vehicle v){
        
        boolean isCov = true;
        
        double battery = v.getInitBattery();
        double traveledDist = 0;
                
        for(Move m : v.getTrace()){
                        
            // update battery
            if (stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 1 && battery < v.getRouteSize() - traveledDist && battery < Global.BATTERY_CAPACITY * 0.1){ // If there is a station and vehicle needs to recharge
                battery = Global.BATTERY_CAPACITY;
            }
            battery -= m.getDepletion();
            traveledDist += m.getDistance();
                            
            // check battery ok
            if (battery < 0) {
                isCov = false;
                break;
            }
        }
                
        return isCov;
    }
    
    private int getNumRecharges(){
        
        int total = 0;
    
        for (Vehicle v : vehicles){
            double battery = v.getInitBattery();
            double traveledDist = 0;
            int numRec = 0;

            for (Move m : v.getTrace()) {
            
                // update battery
                if (stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 1 && battery < v.getRouteSize() - traveledDist && battery < Global.BATTERY_CAPACITY * 0.1){ // If there is a station and vehicle needs to recharge
                    battery = Global.BATTERY_CAPACITY;
                    numRec++;
                }
                battery -= m.getDepletion();
                traveledDist += m.getDistance();

            }
            total += numRec;
            //System.out.println("vehicle " + v.getID() + ": " + numRec);
        }
        return total;
    }
}
