package station;

public class Posicao {
	//Classe que representa um posicao da matriz.
	// Armaze as soluções apresentadas no mapa global.
	int i;
	int j;
	public void main(int i,int j) {
		// TODO Auto-generated method stub
		this.i=i;
		this.j=j;
	}
	public void setPosicao(int i,int j) {
		this.i=i;
		this.j=j;
	}
	public int getI() {
		return this.i;
	}
	public int getJ() {
		return this.j;
	}

}
