package station;
import java.util.HashMap;
import java.util.Map;

public class Formiga {
	int id;
	int bestSolucao;
	int mapaCaminho [][];
	// caminho percorrido pela formiga
	Map <Integer,Integer> caminho = new HashMap<Integer,Integer>();
	
	
	public Formiga(int id, int bestSolucao,int GRID) {
		//mapa com o estado do mapa global para essa formiga.
		mapaCaminho = new int[GRID][GRID];
		this.id=id;
		this.bestSolucao = bestSolucao;
		for(int i=0;i<GRID;i++) {
			for(int j=0;j<GRID;j++) {
				mapaCaminho[i][j]=0;
			}
		}
	}
	//define uma posicao no mapa das formigas.
	public void setPosicao(int i, int j) {
		mapaCaminho[i][j]=1;
	}
	
	public int getPosicao(int i,int j) {
		//retorna o estado da posicao
		return mapaCaminho[i][j];
	}
	public int getId() {
		return id;
	}

	
	

}
