/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

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
    double bestScore[][];
    int bestSolucao[][];
    int numCovVehicles, numStations;
    double feromonioInicial=10;
    int foMelhorSolucao=99999,iterMax=20;
    double alfa=0.5, beta=25,fatorEvaporacao=0.2;
    int NV, GRID,iter=0,entrou=0;
    int numF=10;
    ArrayList<Vehicle> vehicles;
    ArrayList<Formiga> formigas;
    Set<Vehicle> posicoes_validas[][]; // set Vehicles in square

    public AntColony(int NV, int GRID, ArrayList<Vehicle> vehicles,Set<Vehicle> vehicles_square[][]) {

        stationVar = new int[GRID][GRID];
        tabelaFeromonios = new double[GRID][GRID];
        probabilidades = new double[GRID][GRID];
        deltaF = new double[GRID][GRID];
        bestSolucao = new int[GRID][GRID];
        bestScore = new double[GRID][GRID];
        coverage = new int[vehicles.size()];
        this.NV = NV;
        this.GRID = GRID;
        this.vehicles = vehicles;
        this.posicoes_validas = vehicles_square;
        this.numCovVehicles = 0;
        this.numStations = 0;
        formigas = new ArrayList<Formiga>();
        
        
        //score local usado por cada formiga
        
        // Cria as formigas
        for(int i=0;i<numF;i++) {
        	formigas.add(new Formiga(i,GRID));
        }
    }
    
    public void calculaProfit(int i,int j) {
    	
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
    public void solve() {
    	
    	//Inicializa a tabela de distribuição de feromônios
    	for(int i =0;i<GRID;i++) {
    		for(int j=0;j<GRID;j++) {
    			tabelaFeromonios[i][j]=feromonioInicial;
    		}
    	}
    	
    	// repete enquanto não atingir o máximo de iterações
    	while(iter<iterMax) {

    		
    		for(int i=0;i<GRID;i++) {
    			for(int j=0;j<GRID;j++) {
    				deltaF[i][j]=0;
    				probabilidades[i][j]=0;
    			}
    		}
    		
    		
    		//Cada formiga explora um caminho de maneira independente
	    	for(Formiga f : formigas) {
	    		
	    		scoreLocal = new double[GRID][GRID];
	    		
	    		//inicializa mapa da formiga
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
	    			//calculada para cada escolha da formiga
	    			scoreLocal=getScore();
	    			//System.out.println("SCORR "+scoreLocal[84][21]);
	    			
		    		double sum = 0.0;
		    		
		    		
		    		for(int i=0;i<GRID;i++) {
		    			for(int j=0;j<GRID;j++) {
		    				if(f.getPosicao(i, j)==0 &&numCovVehicles < NV) {
		    					sum+=Math.pow(tabelaFeromonios[i][j],alfa)*Math.pow(scoreLocal[i][j],beta);
		    				}
		    			}
		    		}
		    		
		    		//System.out.println(sum);
		    		// Soma está convergindo para zero na segunda formiga.
		    		//possível problema, sai da sequencia independente se ter ou não concluido.
		    		
		    		if(sum==0) {
		    			//System.out.println("Soma zero: "+sum);
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
		    		
		    		// ----------------------------------------
					double [] vetorProb = new double[GRID*GRID];
					double [] escala = new double[GRID*GRID];
					int cont=0;
					
					for(int i=0;i<GRID;i++) {
						for(int j=0;j<GRID;j++) {
							//System.out.println("entrouuu "+cont);
							vetorProb[cont]=probabilidades[i][j];
							cont++;
							
						}
					}
					
					int posValida=0;
					while(posValida==0) {
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
									if(posicoes_validas[i][j].size()>0) {
										posValida=1;
//										System.out.println("posociao"+ i+";"+j);
//										System.out.println("score"+scoreLocal[i][j]);
										f.setPosicao(i, j);
										f.setMapaScore(i, j, scoreLocal[i][j]);
										stationVar[i][j]=1;
										numStations++;
									}
									//realiza um novo sorteio
								}
								cont++;
							}
						}
					}
					
					//--------------------------------------
					//System.out.println();
					updateCoverage();
					
                }

	    		//System.out.println("Veiculos cobertos "+numCovVehicles);
	    		if(numStations<foMelhorSolucao) {
	    			System.out.println("Atualizou melhor solucao "+numStations+" Antiga FO Melhor solucao: "+foMelhorSolucao);
	    			foMelhorSolucao=numStations;
	    			//f.resetarMapa(GRID);
	    			for(int i=0;i<GRID;i++) {
	    				for(int j=0;j<GRID;j++) {
	    					iter=0;
	    					//zera posicoes
	    					bestSolucao[i][j]=0;
	    					bestScore[i][j]=0;
	    					///
	    					bestSolucao[i][j]=f.getPosicao(i, j);
	    					bestScore[i][j]=f.getMapaScore(i, j);
	    				}
	    			}
	    		}
	    		else {
	    			System.out.println("Não atualizou melhor solucao "+numStations+" Melhor solucao: "+foMelhorSolucao);
	    			
	    		}

	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				//TO-DO: verificar a modicaçãoo f.getNumeroEstacoes()==foMelhorSolucao
	    				if(stationVar[i][j]==1) {
	    					//System.out.println("FO melhor solucao: "+foMelhorSolucao+"Num Stations: "+numStations+"F.get"+f.getNumeroEstacoes());
	    					//deposito de feromonio de acordo com a qualidade da solucao
	    					// System.out.println("SCR>> "+scoreLocal[i][j]+);
	    					
	    					deltaF[i][j]+=bestScore[i][j]*(1/(1+(foMelhorSolucao-numStations)/foMelhorSolucao));
	    					//System.out.println("DeltaF "+deltaF[i][j]);
	    				}
	    			}
	    		}
	    		
	    		//Atualiza feromonios
	    		for(int i=0;i<GRID;i++) {
	    			for(int j=0;j<GRID;j++) {
	    				
	    				tabelaFeromonios[i][j]= tabelaFeromonios[i][j]*fatorEvaporacao+deltaF[i][j];
	    				//System.out.println("Nova tabela feromonios "+tabelaFeromonios[i][j]+"fp"+fatorEvaporacao);
	    				
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
            try {
				writeToFile(String.valueOf(numStations));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Output
            for (int i = 0; i < GRID; i++) {
                for (int j = 0; j < GRID; j++) {
                    if (stationVar[i][j] == 1) {
                        System.out.print(i + ";" + j + ";;");
                    }
                }
            }

//            System.out.println("\n");
//            
//            for (int i = 0; i < GRID; i++) {
//                for (int j = 0; j < GRID; j++) {
//                    if (stationVar[i][j] == 1){
//                        System.out.print("x");
//                    }
//                    else System.out.print(" ");
//                }
//                System.out.println();
//            }
        
    }

    public void writeToFile(String estacoes) 
    		  throws IOException {
    			String fileName = "resultado.txt";
    		    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    		    writer.append("NE: "+estacoes+"\n");
    		     
    		    writer.close();
    		}
    
//    public void writeToFilePosicoes(String estacoes,int [][] posicoes) 
//  		  throws IOException {
//  			String fileName = "posicao.txt";
//  		    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//  		    
//  		    for(int i=0;i<GRID;i++) {
//  		    	for(int j=0;j<GRID;j++) {
//  		    		posicoes
//  		    	}
//  		    }
//  		    writer.append("Posicoes: "+estacoes+"\n");
//  		     
//  		    writer.close();
//  		}
    
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
//        		if(posicoes_validas[i][j].size()>0) {
//					//penaliza posicoes vazias
//					//probabilidades[i][j]=-1;
//					score[i][j]=score[i][j]+0.2;
//				}
//        		
//        	}
//        }

		return score;
        
    	
    }
    
   //-----------------------------------------------------------------------------------------------------------
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
