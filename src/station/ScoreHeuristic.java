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
public class ScoreHeuristic {

    int stationVar[][];
    int coverage[];
    int numCovVehicles, numStations;
    int NV, GRID;
    ArrayList<Vehicle> vehicles;

    public ScoreHeuristic(int NV, int GRID, ArrayList<Vehicle> vehicles) {

        stationVar = new int[GRID][GRID];
        coverage = new int[vehicles.size()];
        this.NV = NV;
        this.GRID = GRID;
        this.vehicles = vehicles;
        this.numCovVehicles = 0;
        this.numStations = 0;
    }

    public void solve() {

        // Constructive Heuristic
        greedyConstruction();

        
        System.out.println("\n*** Solution ***\n");

        System.out.println("Number of stations:" + numStations);
        System.out.println("Number of recharges:" + getNumRecharges());

        // Output
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                if (stationVar[i][j] == 1) {
                    System.out.print(i + ";" + j + ";;");
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

    private void greedyConstruction() {

        int score[][] = new int[GRID][GRID];

        while (numCovVehicles < NV) {
            
            //System.out.println("coverage: " + (double)numCovVehicles/(double)NV);
            
            // clean scores
            for (int i = 0; i < GRID; i++) {
                for (int j = 0; j < GRID; j++) {
                    score[i][j] = 0;
                }
            }
            // update scores
            for (Vehicle v : vehicles) {

                if (coverage[v.getID()] == 0) {
                    double battery = v.getInitBattery();
                    double traveledDist = 0;

                    for (Move m : v.getTrace()) {

                        if (battery < v.getRouteSize() - traveledDist  && battery < Global.BATTERY_CAPACITY * 0.1 && stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 0) {
                            score[m.getSquare().getCoordX()][m.getSquare().getCoordY()]++;
                            battery = Global.BATTERY_CAPACITY;
                        } 
                        else if (battery < v.getRouteSize() - traveledDist  && battery < Global.BATTERY_CAPACITY * 0.1 && stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 1) {
                            battery = Global.BATTERY_CAPACITY;
                        }
                        battery -= m.getDepletion();
                        traveledDist += m.getDistance();
                    }
                }
            }

            // place highest
            int max = -1, best_i = -1, best_j = -1;
            for (int i = 0; i < GRID; i++) {
                for (int j = 0; j < GRID; j++) {
                    if (score[i][j] > max) {
                        max = score[i][j];
                        best_i = i;
                        best_j = j;
                    }
                }
            }

            // deploy 
            stationVar[best_i][best_j] = 1;
            numStations++;
            //System.out.println(best_i + " " + best_j);
            //System.out.println(numStations);

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

    private boolean isCovered(Vehicle v) {

        boolean isCov = true;

        double battery = v.getInitBattery();
        double traveledDist = 0;

        for (Move m : v.getTrace()) {
            
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
