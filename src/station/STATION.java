/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author fernanda
 */
public class STATION {
    
    
    
    int NV; // number of vehicles   
    ArrayList<Vehicle> vehicles; // set of vehicles
    Square [][] squares; // set of squares
    Set<Vehicle> vehiclesInSquare[][]; // set Vehicles in square
    
    DLHeuristic dl;
    ScoreHeuristic score;
    AntColony antcolony;
    
    long seed = 10000; // seed 
    Random r = new Random(seed); // random number generator
    
    @SuppressWarnings("unchecked")
	public STATION(String numVehicles, String file, String algo, String depletion){
        
        this.NV = Integer.parseInt(numVehicles);        
        this.vehicles = new ArrayList<>();
        this.squares = new Square[Global.GRID_N][Global.GRID_N];
        this.vehiclesInSquare = new Set[Global.GRID_N][Global.GRID_N]; 
        Global.setDepletionRate(Double.parseDouble(depletion));
        
        // creating vehicles
        for (int i = 0; i < NV; i++) vehicles.add(new Vehicle(i, Global.BATTERY_CAPACITY * r.nextDouble())); // each vehicle starts with a random initial battery
        
        // creating squares
        for (int i = 0; i < Global.GRID_N; i++){
            for (int j = 0; j < Global.GRID_N; j++){
                squares[i][j] = new Square((i*Global.GRID_N+j), i, j);
            }
        }
        
        System.out.println("Reading file...");
        readFile(file);
        System.out.println("Finish!");
        
        /*********************************  PRE PROCESS ***********************************/
        
        System.out.println("\n\n*** TRACE INFO ***\n");
        
        // vehicles in square
        int numUsedSquares = 0;
       
        
        for (int i = 0; i < Global.GRID_N; i++) 
            for (int j = 0; j < Global.GRID_N; j++)
                vehiclesInSquare[i][j] = new HashSet<>();
        
        // Pre
        for (Vehicle v : vehicles) {
            for (Move m : v.getTrace()){
                vehiclesInSquare[m.getSquare().getCoordX()][m.getSquare().getCoordY()].add(v);
            }
        }
        
        for (int i = 0; i < Global.GRID_N; i++) {
            for (int j = 0; j < Global.GRID_N; j++){
                //System.out.print("Square [" + i + "][" + j + "]: " + vehiclesInSquare[i][j].size());
                /*for (Vehicle v : vehiclesInSquare[i][j]){
                    System.out.print(v.getID() + " ");
                }*/
                //System.out.println();
                if (vehiclesInSquare[i][j].size() > 0) numUsedSquares++;
            }
        }
        System.out.println("Number of squares with vehicles: " + numUsedSquares);
        
        // route size distribution
        int []distribution = new int[50];
        double minDist = 1000000, maxDist = 0;
        int numZero = 0; 
        int numRecharge = 0; // number of vehicles which will need to recharge
        for (Vehicle v : vehicles) {
            if (v.getRouteSize() > v.getInitBattery()){
                numRecharge++;
            }
            if (v.getRouteSize() > maxDist){
                maxDist = v.getRouteSize();
            }
            if (v.getRouteSize() < minDist){
                minDist = v.getRouteSize();
            }
            int pos = (int) Math.ceil(v.getRouteSize()/(double)1000);
            distribution[pos]++;
            if (v.getRouteSize() == 0) numZero++;
        }
        //System.out.println("Min distance of a vehicle: " + minDist);
        //System.out.println("Max distance of a vehicle: " + maxDist);
        //System.out.println("Number of vehicles with zero distance: " + numZero);
        //System.out.println("Number of vehicles which will need recharge: " + numRecharge);
        
        //System.out.println("\n Route size distribution");
//        for (int i = 0; i < 50; i++){
//            System.out.printf("%6d ", i);
//        }
//        System.out.print("\n");
//        for (int i = 0; i < 50; i++){
//            System.out.printf("%6d ", distribution[i]);
//        }
        
        
        
       // System.out.println("\n\n******************\n");
        
        /*********************************************************************************/
        

        System.out.println("Starting algorithm...");
        run(Integer.parseInt(algo));
        System.out.println("\nFinish!");
    }
    
    private void readFile(String file){
    
        try{
            
            int COUNT, former, id, x, y;
            double timeIn, timeStay, distance;
            String line;
            StringTokenizer st;        
            Vehicle v;
            Move m;
            Square s;
            
            try (BufferedReader fileIn = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                
                line = fileIn.readLine(); 
                
                // vehicle count
                COUNT = former = 0;
                
                while (line != null) {
                    
                    st = new StringTokenizer(line, ";: \t");
                                        
                    id = Integer.parseInt(st.nextToken()); // vehicle ID
                    
                    if (former != id) {
                        COUNT++;
                        former = id;
                    } 
                    
                    if (COUNT >= NV) break;
                                                            
                    timeIn = Double.parseDouble(st.nextToken()); // time vehicle enters the cell
                    x = (int) Double.parseDouble(st.nextToken()); // Grid square X
                    y = (int) Double.parseDouble(st.nextToken()); // Grid square Y
                    distance = Double.parseDouble(st.nextToken()); // distance vehicle travels in cell
                    timeStay = Double.parseDouble(st.nextToken()) ; // time vehicle stays in cell (seconds)
                    
                    v = vehicles.get(COUNT);
                    s = squares[x][y];
                    m = new Move(s, timeIn, timeIn+timeStay, distance);
                    v.addMove(m);
                    
                    line = fileIn.readLine();                     
                }
                
                fileIn.close();                    
            }
        }
        catch(IOException e){
            System.err.print(e.toString());
        }
    }
    
    private void run(int algo){
                
        if (algo == 0){
            dl = new DLHeuristic(NV, Global.GRID_N, vehicles);
            dl.solve();            
        }
        else if (algo == 1){
            score = new ScoreHeuristic(NV, Global.GRID_N, vehicles);
            score.solve();            
        }
        else if(algo == 2) {
        	antcolony = new AntColony(NV, Global.GRID_N, vehicles,  vehiclesInSquare);
            antcolony.solve();
        }
        else{
                       
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // 0 - number of vehicles
        // 1 - file
        // 2 - algorithm (0 - DL / 1 - Score/ 2- AntColony)
        // 3 - depletion rate ( Default 1)
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        long tempoInicial = System.currentTimeMillis();
        
        STATION st = new STATION(args[0], args[1], args[2], args[3]);
        
        System.out.println("\n" + df.format((System.currentTimeMillis() - tempoInicial)/1000.00));
    }
    
}
