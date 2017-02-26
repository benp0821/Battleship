import java.awt.*;
import javax.swing.ImageIcon;

public class ShipPiece {
	private Image shipPieceAlive;
	private boolean shipIsDead;
	boolean isPlayer1;

	/*
	 * Constructor that has a boolean to determine which player the ship piece
	 * belongs to. false is player 2, true is player 1
	 */
	public ShipPiece(boolean isPlayer1) {
		this.isPlayer1 = isPlayer1;
		// sets the image based on which player it belongs too
		if (isPlayer1)
			shipPieceAlive = new ImageIcon("Player1.png").getImage();
		else
			shipPieceAlive = new ImageIcon("Player2.png").getImage();
		shipIsDead = false;
	}

	/*
	 * sets the image based on the file name
	 */
	public void setShipImage(String file) {
		shipPieceAlive = new ImageIcon(file).getImage();

	}

	/*
	 * returns the ship piece's image
	 */
	public Image getShipImage() {
		return shipPieceAlive;
	}

	/*
	 * Destroys the ship piece by setting shipIsDead to true and changing the
	 * image to the damaged image for the player
	 */
	public void destroy() {
		shipIsDead = true;
		if (isPlayer1) {
			setShipImage("Player1Hit.png");
		} else {
			setShipImage("Player2Hit.png");
		}
	}

	/*
	 * Returns if the ship piece is destroyed
	 */
	public boolean isDestroy() {
		return shipIsDead;
	}

}
