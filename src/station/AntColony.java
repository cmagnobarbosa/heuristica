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
    double scoreLocal[][];
    double deltaF[][];
    double tabelaFeromonios[][];
    double probabilidades[][];
    int bestSolucao[][];
    int numCovVehicles, numStations;
    double feromonioInicial=10;
    int foMelhorSolucao=99999,iterMax=5;
    double alfa=0.5, beta=20,fatorEvaporacao=2;
    int NV, GRID,iter=0,entrou=0;
    int numF=5;
    ArrayList<Vehicle> vehicles;
    ArrayList<Formiga> formigas;

    public AntColony(int NV, int GRID, ArrayList<Vehicle> vehicles) {

        stationVar = new int[GRID][GRID];
        tabelaFeromonios = new double[GRID][GRID];
        probabilidades = new double[GRID][GRID];
        deltaF = new double[GRID][GRID];
        bestSolucao = new int[GRID][GRID];
        coverage = new int[vehicles.size()];
        this.NV = NV;
        this.GRID = GRID;
        this.vehicles = vehicles;
        this.numCovVehicles = 0;
        this.numStations = 0;
        formigas = new ArrayList<Formiga>();
        
        
        //score local usado por cada formiga
        
        // Cria as formigas
        for(int i=0;i<numF;i++) {
        	formigas.add(new Formiga(i,GRID));
        }
    }
    
 
    public void solve() {
    	
    	//Inicializa a tabela de distribuição de feromônios
    	for(int i =0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			tabelaFeromonios[i][j]=feromonioInicial;
    		}
    	}
    	
    	while(iter<iterMax) {

    		
    		for(int i=0;i<GRID;i++) {
    			for(int j=0;j<GRID;j++) {
    				deltaF[i][j]=0;
    				probabilidades[i][j]=0;
    			}
    		}
    		
    		
    		//Cada formiga explora um caminho de maneira independente
	    	for(int cont_f=0;cont_f<formigas.size();cont_f++) {
	    		
	    		scoreLocal = new double[GRID][GRID];
	    		Formiga f=formigas.get(cont_f);
	    		f.resetarMapa(GRID);
	    		numCovVehicles = 0;
	            numStations = 0;
	    		System.out.println("Formiga: "+f.getId()+ " Cov: "+numCovVehicles);
	    		//zerando variaveis
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				scoreLocal[i][j] = 0;
	    				probabilidades[i][j] = 0;
	    				stationVar[i][j]=0;
	    			}
	    		}
	    		//zera cobertura dos carros
	    		for(int car=0;car<coverage.length;car++) {
	    			coverage[car]=0;
	    		}
	    		
		        //repete enquanto existir veículos sem cobertura
	    		while(numCovVehicles < NV) {
	   
	    			for(int i=0;i<GRID;i++) {
	    				for(int j=0;j<GRID;j++) {
	    					scoreLocal[i][j]=0;
	    				}
	    			}
	    			
	    			//informação heurística
	    			scoreLocal=getScore();
	    			//System.out.println("SCORR "+scoreLocal[84][21]);
	    			
		    		double sum = 0.0;
		    		
		    		
		    		for(int i=0;i<GRID;i++) {
		    			for(int j=0;j<GRID;j++) {
		    				if(f.getPosicao(i, j)==0 &&numCovVehicles < NV) {
		    					double fero_temp = tabelaFeromonios[i][j];
		    					double score_temp = scoreLocal[i][j];
		    					sum+=Math.pow(fero_temp,alfa)*Math.pow(score_temp,beta);
		    				}
		    			}
		    		}
		    			//System.out.println(sum);
		    		// Soma está convergindo para zero na segunda formiga.
		    		//possível problema, sai da sequencia independente se ter ou não concluido.
		    		
		    		if(sum==0) {
		    			System.out.println("Soma zero: "+sum);
		    			break;
		    		}
		    		
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
					double [] escala = new double[GRID*GRID];
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
					}
					cont=0;
					for(int i=0;i<GRID;i++) {
						for(int j=0;j<GRID;j++) {
							if(cont==k) {
								f.setPosicao(i, j);
								stationVar[i][j]=1;
								numStations++;
							}
							cont++;
						}
					}
					//--------------------------------------
					//System.out.println();
					updateCoverage();
					
                }

	    		System.out.println("Veiculos cobertos "+numCovVehicles);
	    		if(f.getNumeroEstacoes()!=0 && f.getNumeroEstacoes()<foMelhorSolucao) {
	    			System.out.println("Atualizou melhor solucao "+numStations+" Antiga FO Melhor solucao: "+foMelhorSolucao);
	    			foMelhorSolucao=f.getNumeroEstacoes();
	    			f.resetarMapa(GRID);
	    			for(int i=0;i<GRID;i++) {
	    				for(int j=0;j<GRID;j++) {
	    					iter=0;
	    					if(stationVar[i][j]==1) {
	    						bestSolucao[i][j]=1;
	    						f.setPosicao(i, j);
	    					}
	    				}
	    			}
	    		}
	    		else {
	    			System.out.println("Não atualizou melhor solucao "+numStations+" Melhor solucao: "+foMelhorSolucao);
	    		}

	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				if(f.getPosicao(i, j)==1) {
	    					//System.out.println("FO melhor solucao: "+foMelhorSolucao+"Num Stations: "+numStations+"F.get"+f.getNumeroEstacoes());
	    					//deposito de feromonio de acordo com a qualidade da solucao
	    					deltaF[i][j]+=scoreLocal[i][j]*(1/(1+(foMelhorSolucao-f.getNumeroEstacoes())/foMelhorSolucao));
	    				}
	    			}
	    		}
	    		
	    		//Atualiza feromonios
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				tabelaFeromonios[i][j]= tabelaFeromonios[i][j]*fatorEvaporacao+deltaF[i][j];
	    				//System.out.println("Nova tabela feromonios "+tabelaFeromonios[i][j]);
	    			}
	    		}
				
	    	}
	    	iter++;
    	}
    	
    	for(int i=0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			stationVar[i][j]=0;
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


    private double[][] getScore() {
    	double score[][] = new double[GRID][GRID];
    	//zerando o score.
//    	for(int i=0;i<GRID;i++) {
//    		for(int j=0;j<GRID;j++) {
//    			score[i][j]=0;
//    		}
//    	}
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

//        for(int i=0;i<GRID;i++) {
//        	for(int j=0;j<GRID;j++) {
////        		if(score[i][j]>1) {
////        			Random gerador = new Random();
////        			int valor = gerador.nextInt(2);
////        			score[i][j]=score[i][j]+valor;
////        		}
//        	}
//        }
        
//        for(int i=0;i<GRID;i++) {
//        	for(int j=0;j<GRID;j++) {
//        		if(score[i][j]==0) {
//        			score[i][j]=0.1;
//        		}
//        		
//        	}
//        }
		return score;
        
    	
    }
    
   //-----------------------------------------------------------------------------------------------------------
    
//     @SuppressWarnings("unused")
// 	private void greedyConstruction() {

//         int score[][] = new int[GRID][GRID];

//         while (numCovVehicles < NV) {
            
//             //System.out.println("coverage: " + (double)numCovVehicles/(double)NV);
            
//             // clean scores
//             for (int i = 0; i < GRID; i++) {
//                 for (int j = 0; j < GRID; j++) {
//                     score[i][j] = 0;
//                 }
//             }
//             // update scores
//             for (Vehicle v : vehicles) {

//                 if (coverage[v.getID()] == 0) {
//                     double battery = v.getInitBattery();
//                     double traveledDist = 0;

//                     for (Move m : v.getTrace()) {

//                         if (battery < v.getRouteSize() - traveledDist  && battery < Global.BATTERY_CAPACITY * 0.1 && stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 0) {
//                             score[m.getSquare().getCoordX()][m.getSquare().getCoordY()]++;
//                             battery = Global.BATTERY_CAPACITY;
//                         } 
//                         else if (battery < v.getRouteSize() - traveledDist  && battery < Global.BATTERY_CAPACITY * 0.1 && stationVar[m.getSquare().getCoordX()][m.getSquare().getCoordY()] == 1) {
//                             battery = Global.BATTERY_CAPACITY;
//                         }
//                         battery -= m.getDepletion();
//                         traveledDist += m.getDistance();
//                     }
//                 }
//             }

//             // place highest
//             int max = -1, best_i = -1, best_j = -1;
//             for (int i = 0; i < GRID; i++) {
//                 for (int j = 0; j < GRID; j++) {
//                 	if(score[i][j]>1) {
//                 		 System.out.println("Score "+score[i][j]);
//                 	}
//                     if (score[i][j] > max) {
//                         max = score[i][j];
//                         //System.out.println("Score "+score[i][j]);
//                         best_i = i;
//                         best_j = j;
//                     }
//                 }
//             }

//             // deploy 
//             stationVar[best_i][best_j] = 1;
//             numStations++;
// //            System.out.println(best_i + " " + best_j);
// //            System.out.println(numStations);

//             // Update coverage
//             updateCoverage();
//         }

//     }

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
