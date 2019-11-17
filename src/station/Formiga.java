package station;

public class Formiga {
	int id;
	int numeroEstacao=0;
	int mapaCaminho [][];

	
	public Formiga(int id,int GRID) {
		//mapa com o estado do mapa global para essa formiga.
		this.id=id;
		mapaCaminho = new int[GRID][GRID];
		for(int i=0;i<GRID;i++) {
			for(int j=0;j<GRID;j++) {
				//mapa de solucoes de cada formiga
				mapaCaminho[i][j]=0;
			}
		}
		
			
	}
	//atualiza a posicao do mapa para conter um estacao.
	public void setPosicao(int i,int j) {
		mapaCaminho[i][j]=1;
		this.numeroEstacao++;
	}
	public int getPosicao(int i, int j) {
		return mapaCaminho[i][j];
	}
	public void setRemoverPosicao(int i,int j) {
		mapaCaminho[i][j]=0;
		this.numeroEstacao--;
	}
	
	public int[][] getMapaCompleto(){
		return mapaCaminho;
	}
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id=id;
	}
	//retorna o número de estacões
	public int getNumeroEstacoes() {
		return this.numeroEstacao;
	}

	
	

}
