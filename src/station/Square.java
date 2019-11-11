/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package station;

/**
 *
 * @author fernanda
 */
public class Square {
    
    int id; // square ID (number between 0 and Global.GRID_N x Global.GRID_N)
    int coordX, coordY; // square coordinates (number between 0 and Global.GRID_N)
    
    Square(int id, int x, int y){
        this.id = id;
        this.coordX = x;
        this.coordY = y;
    }
    
    int getID(){
        return this.id;
    }
    
    int getCoordX(){
        return this.coordX;
    }
    
    int getCoordY(){
        return this.coordY;
    }
}
