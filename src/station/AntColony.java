/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fernanda
 */
public class AntColony {

    int stationVar[][];
    int coverage[];
    int scoreLocal[][];
    double deltaF[][];
    double tabelaFeromonios[][];
    double probabilidades[][];
    double escala [];
    int bestSolucao[][];
    int numCovVehicles, numStations;
    double feromonioInicial=1;
    int foMelhorSolucao=999999,iterMax=1;
    double alfa=1, beta=0.5,fatorEvaporacao=0.9;
    int NV, GRID,iter=0;
    ArrayList<Vehicle> vehicles;
    ArrayList<Formiga> formigas;

    public AntColony(int NV, int GRID, ArrayList<Vehicle> vehicles) {

        stationVar = new int[GRID][GRID];
        tabelaFeromonios = new double[GRID][GRID];
        probabilidades = new double[GRID][GRID];
        deltaF = new double[GRID][GRID];
        bestSolucao = new int[GRID][GRID];
        escala = new double[GRID*GRID];
        coverage = new int[vehicles.size()];
        this.NV = NV;
        this.GRID = GRID;
        this.vehicles = vehicles;
        this.numCovVehicles = 0;
        this.numStations = 0;
        formigas = new ArrayList<Formiga>();
        //score local usado por cada formiga
        scoreLocal = new int[GRID][GRID];
        formigas.add(new Formiga(1,GRID));
        //formigas.add(new Formiga(2,GRID));
    }
    
    public void imprimeMatriz(int [][] matriz, int tamanho) {
    	for(int i=0;i<tamanho;i++) {
    		for(int j=0;j<tamanho;j++) {
    			System.out.println("Matriz:"+matriz[i][j]);
    		}
    	}
    }
    public void solve() {

        // Constructive Heuristic
        //greedyConstruction();
    	
    	//Inicializa a tabela de distribuição de feromônios
    	for(int i =0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			tabelaFeromonios[i][j]=feromonioInicial;
    		}
    	}
    	while(iter<iterMax) {
    		iter++;
    		
//    		for(int i=0;i<GRID;i++) {
//    			for(int j=0;j<GRID;j++) {
//    				deltaF[i][j]=0;
//    			}
//    		}
    		
	    	for(Formiga f: formigas) {
	    		
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				scoreLocal[i][j]=0;
	    				stationVar[i][j]=0;
	    				probabilidades[i][j]=0;
	    			}
	    		}
	    		numCovVehicles = 0;
	            numStations = 0;
	    		while(numCovVehicles < NV) {
		    		getScore();
		    		double sum =0;
		    		for(int i=0;i<GRID;i++) {
		    			for(int j=0;j<GRID;j++) {
		    				sum+=Math.pow(tabelaFeromonios[i][j],alfa)*Math.pow(scoreLocal[i][j],beta);
		    			}
		    		}
		    		
		    		//System.out.println("Soma "+sum);
		    		
		    		
		    		for(int i=0;i<GRID;i++) {
		    			for(int j=0;j<GRID;j++) {
		    				if(f.getPosicao(i, j)==0) {
		    					probabilidades[i][j]=(Math.pow(tabelaFeromonios[i][j],alfa)*Math.pow(scoreLocal[i][j],beta))/sum;
		    					
		    				}else {
		    					probabilidades[i][j]=0;
		    				}
		    			}
		    		}
					double [] vetorProb = new double[GRID*GRID];
					int cont=0;
					
					for(int i=0;i<GRID;i++) {
						for(int j=0;j<GRID;j++) {
							vetorProb[cont]=probabilidades[i][j];
							cont++;
						}
					}
					
					//Roleta -------
					for(int i=0;i<GRID*GRID;i++) {
						escala[i]=0;
					}
					escala[0]=vetorProb[0];
					for(int i=1;i<GRID*GRID;i++) {
						escala[i]=escala[i-1]+vetorProb[i];
					}
					Random r = new Random(); 
					double aux = r.nextDouble();
					int k=0;
					while(escala[k]<aux) {
						k++;
						//System.out.println("Escala: "+escala[k]+"/"+aux);
					}

					cont=0;
					for(int i=0;i<GRID;i++) {
						for(int j=0;j<GRID;j++) {
							if(cont==k) {
								//System.out.println("POS sorteada i:"+i+" J:"+j);
								f.setPosicao(i, j);
								stationVar[i][j]=1;
								numStations++;
							}
							cont++;
						}
					}
					//--------------------------------------
					updateCoverage();
	    		}
	    		
	    		if(numStations<foMelhorSolucao) {
	    			foMelhorSolucao=numStations;
	    			for(int i=0;i<GRID;i++) {
	    				for(int j=0;j<GRID;j++) {
	    					bestSolucao[i][j]=0;
	    					if(stationVar[i][j]==1) {
	    						bestSolucao[i][j]=1;
	    					}
	    				}
	    			}
	    		}
				
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				if(f.getPosicao(i, j)==1) {
	    					deltaF[i][j]+=scoreLocal[i][j]*(1/(1+(foMelhorSolucao-numStations)/foMelhorSolucao)); 
	    				}
	    			}
	    		}
	    		
	    		//Atualiza feromonios
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				tabelaFeromonios[i][j]= tabelaFeromonios[i][j]*fatorEvaporacao+deltaF[i][j];
	    			}
	    		}
				
	    	}
    	}
    	
    	for(int i=0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			if(bestSolucao[i][j]==1) {
    				stationVar[i][j]=1;
    			}
    		}
    	}
    	numStations=foMelhorSolucao;
    	
    	
    		
    		
        
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
    private void getScore() {
    	int score[][] = new int[GRID][GRID];
    	
    	//zerando o score.
    	for(int i=0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			score[i][j]=0;
    		}
    	}
    	   // update scores
    	//stationVar[12][34]=1;
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
        
        for(int i=0;i<GRID;i++) {
        	for(int j=0;j<GRID;j++) {
        		scoreLocal[i][j]=score[i][j];
        	}
        }
        
    	
    }
    
   //-----------------------------------------------------------------------------------------------------------
    
    @SuppressWarnings("unused")
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
                	if(score[i][j]>1) {
                		 System.out.println("Score "+score[i][j]);
                	}
                    if (score[i][j] > max) {
                        max = score[i][j];
                        //System.out.println("Score "+score[i][j]);
                        best_i = i;
                        best_j = j;
                    }
                }
            }

            // deploy 
            stationVar[best_i][best_j] = 1;
            numStations++;
//            System.out.println(best_i + " " + best_j);
//            System.out.println(numStations);

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
