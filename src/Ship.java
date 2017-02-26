import java.awt.Point;
import java.util.ArrayList;

public class Ship {

	private ShipPiece[] pieces;
	private Point startingPosition;

	/*
	 * Constructor. Sets pieces equal to the list parameter
	 */
	Ship(ShipPiece[] list) {
		pieces = list;
		startingPosition = new Point(0,0);
	}
	
	Ship(ArrayList<ShipPiece> list){
		pieces = list.toArray(new ShipPiece[0]);
		startingPosition = new Point(0,0);
	}

	/*
	 * If all the ship pieces are dead, it returns that the ship is dead
	 */
	public boolean checkIfDead() {
		boolean isDead = true;
		for (int i = 0; i < pieces.length; i++) {
			if (!pieces[i].isDestroy()) {
				isDead = false;
			}
		}
		return isDead;
	}

	/*
	 * returns the array of ship pieces
	 */
	public ShipPiece[] getShipPieces() {
		return pieces;
	}
	
	public void setStartingOffGridPosition(Point sp){
		startingPosition = sp;
	}
	
	public Point getStartingOffGridPosition(){
		return startingPosition;
	}
}