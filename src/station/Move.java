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
public class Move {
    
    Square s; // square of the move
    double timeIn; // time the vehicle enters the square
    double timeOut; // time the vehicle leaves the square
    double distance; // distance travelled inside the square
    
    Move(Square s, double timeIn, double timeOut, double distance){
        this.s = s;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.distance = distance;
    }
    
    Square getSquare(){
        return this.s;
    }
    
    double getTimeIn(){
        return this.timeIn;
    }
    
    double getTimeOut(){
        return this.timeOut;
    }
    
    double getTimeStay(){
        return this.timeOut - this.timeIn;
    }
    
    double getDistance(){
        return this.distance;
    }
    
    double getDepletion(){
        return this.distance * Global.DEPLETION_RATE;
    }
}
