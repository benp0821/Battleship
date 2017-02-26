import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class SmallGrid extends JPanel {
	private static final long serialVersionUID = 1L;
	private Object[][] array;
	private BufferedImage gridImage;
	public static final int X_ORIGIN = 23; // X coordinate of the top left
	public static final int Y_ORIGIN = 39; // y coordinate of the top left
	public static final int TILE_SIZE = 20; // size of the tile spaces
	public static final int BORDER_SIZE = 3; // size of the border between tiles
	public static final int PIECE_SIZE = 18; // size of the pieces that appear

	/*
	 * Constructor that takes a 2d array object
	 */
	public SmallGrid(Object[][] array) {
		this.array = array;
		
		// sets the background to white
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension((X_ORIGIN + 2 + (TILE_SIZE+BORDER_SIZE)*array.length), 
				Y_ORIGIN+ 2 + ((TILE_SIZE+BORDER_SIZE)*array.length)));
		setSize(getPreferredSize());
		
		// loads the grid file
		try {
			gridImage = ImageIO.read(new File("gridSmallLabels.png"));
		} catch (IOException e) {
			System.out.println("Failed to load image");
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// draws the grid image
		g2.drawImage(gridImage, 0, 15, this);

		// loops through the array
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				// if there is a ship piece at the location
				if ((array[i][j]).getClass().getName().equals("ShipPiece")) {
					// draw the image of the ship piece at the proper grid
					// location
					g2.drawImage(((ShipPiece) array[i][j]).getShipImage(), 
							X_ORIGIN + 2 + ((TILE_SIZE + BORDER_SIZE) * i) + BORDER_SIZE/2,
							Y_ORIGIN + 2 + ((TILE_SIZE + BORDER_SIZE) * j) + BORDER_SIZE/2,
							PIECE_SIZE, PIECE_SIZE, this);
				}
			}
		}
	}

	/*
	 * Returns the array
	 */
	public Object[][] getArray() {
		return array;
	}

	/*
	 * Sets the array
	 */
	public void setArray(Object[][] array) {
		this.array = array;
	}

}
