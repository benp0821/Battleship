import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GridCreator extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage gridImage = null;
	private Object[][] gridArray;
	private Ship[] shipArray;
	private JPanel[] panelArray;
	private JButton endSetup, randomizeShipsBtn;
	private JFrame window;
	private volatile boolean setupOver = false;
	public static final int X_ORIGIN = 54;
	public static final int Y_ORIGIN = 56;
	public static final int TILE_SIZE = 47;
	public static final int BORDER_SIZE = 5;
	public static boolean currentlyPlacingShip = false;

	public GridCreator(Ship[] shipArray, JFrame app) {
		this(shipArray, 10, app);
	}

	public GridCreator(Ship[] shipArray, int gridSize, JFrame app) {
		this(shipArray, gridSize, "gridLabels.png", app);
	}

	public GridCreator(Ship[] shipArray, int gridSize, String path, JFrame app) {
		setLayout(null);
		setBackground(Color.white);
		setLocation(0,0);
		window = app;

		Object[][] grid = new Object[gridSize][gridSize];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = 1;
			}
		}

		gridArray = grid;
		this.shipArray = shipArray;
		panelArray = new JPanel[shipArray.length];

		try {
			gridImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("Failed to load image");
		}

	}

	/*
	 * Does all the work to setup the grid.
	 */
	public void setup() {
		int largestShipSize = 0;
		for (int i = 0; i < shipArray.length; i++){
			int temp = shipArray[i].getShipPieces().length;
			if (temp > largestShipSize){
				largestShipSize = temp;
			}
		}
		
		int windowWidth = X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) + (2 * BORDER_SIZE) + 50
				+ ((largestShipSize + 1) * TILE_SIZE);
		int windowHeight = Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * (gridArray.length + 1));
		if (windowHeight < 2 * TILE_SIZE + (shipArray.length * (TILE_SIZE + BORDER_SIZE + 2))) {
			windowHeight = 2 * TILE_SIZE + (shipArray.length * (TILE_SIZE + BORDER_SIZE + 2));
		}
		window.setPreferredSize(new Dimension(windowWidth, windowHeight));
		window.setMinimumSize(new Dimension(windowWidth, windowHeight));
		window.pack();
		setSize(window.getContentPane().getSize());

		// creates a label with the grid image and adds it to the screen
		JLabel gridLabel = new JLabel(new ImageIcon(gridImage));
		gridLabel.setSize(X_ORIGIN + gridArray.length + 1 + ((TILE_SIZE + BORDER_SIZE) * gridArray.length),
				Y_ORIGIN + gridArray.length + 1 + ((TILE_SIZE + BORDER_SIZE) * (gridArray.length)));
		gridLabel.setLocation(0, 0);
		gridLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gridLabel.setVerticalAlignment(SwingConstants.TOP);
		add(gridLabel);

		int buttonXPos = gridLabel.getWidth();

		randomizeShipsBtn = new JButton("Randomize Grid");
		randomizeShipsBtn.setBounds(buttonXPos, 0, window.getWidth() - buttonXPos, TILE_SIZE - 5);
		randomizeShipsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Random rand = new Random();
				for (int i = 0; i < panelArray.length; i++) {
					panelArray[i].setLocation(shipArray[i].getStartingOffGridPosition());
				}
				for (int i = 0; i < panelArray.length; i++) {
					int timeout = 0;
					while (timeout < 500
							&& shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())) {
						int x = rand.nextInt(gridArray.length);
						int y = rand.nextInt(gridArray.length);
						timeout++;
						leftClick(i, x, y);
						if (rand.nextInt(2) == 0
								&& !shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())) {
							rightClick(i, x, y);
						}
					}
				}
			}
		});
		add(randomizeShipsBtn);
		randomizeShipsBtn.setVisible(true);
		repaint();

		// creates a button that ends setup when pressed
		endSetup = new JButton("Finish");
		endSetup.setBounds(buttonXPos, TILE_SIZE - 5, window.getWidth() - buttonXPos, window.getHeight());
		endSetup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setupOver = true;
			}
		});
		add(endSetup);
		// sets the visibility of the button to false
		endSetup.setVisible(false);

		// loops through all the ships
		for (int j = 0; j < shipArray.length; j++) {
			final int shipNum = j;

			// creates a panel with an X Axis box layout for the ship
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setOpaque(false);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			panel.add(Box.createRigidArea(new Dimension(0, 0)));

			// loops through the ship pieces in the ship
			for (int i = 0; i < shipArray[j].getShipPieces().length; i++) {
				// adds labels containing each image to the panel
				ImageIcon icon = new ImageIcon(shipArray[j].getShipPieces()[i].getShipImage());
				JLabel label = new JLabel(icon);
				panel.add(label);
				panel.add(Box.createRigidArea(new Dimension(BORDER_SIZE + 2, 0)));
				// places the panel off to the side of the grid
				// panel.setLocation(650, 50 + (j * 50));

			}
			panel.setLocation(X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) + (2 * BORDER_SIZE) + 50,
					TILE_SIZE + BORDER_SIZE + 2 + (j * (TILE_SIZE + BORDER_SIZE + 2)));
			panel.setSize(shipArray[j].getShipPieces().length * (TILE_SIZE + BORDER_SIZE), TILE_SIZE);
			shipArray[shipNum].setStartingOffGridPosition(panel.getLocation());
			add(panel);
			panelArray[j] = panel;
			setComponentZOrder(panel, 0);

			panel.addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					// when the left mouse button is down the panel is moved to
					// the mouse location
					if (SwingUtilities.isLeftMouseButton(e)) {
						JPanel component = (JPanel) e.getComponent().getParent().getParent();
						Point pt = new Point(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), component));
						panel.setLocation((int) pt.getX() - (TILE_SIZE / 2), (int) pt.getY() - (TILE_SIZE / 2));
						currentlyPlacingShip = true;
					}
				}

			});
			// adds a mouse listener to the panel
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					// gets the coordinates of the mouse in terms of the window
					JPanel component = (JPanel) e.getComponent().getParent().getParent();
					Point pt = new Point(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), component));
					int counter1 = 0;
					int counter2 = 0;

					// calculates the position in the grid array based on the
					// mouse coordinates
					int value = (int) pt.getX();

					while (X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter1) < value) {
						counter1++;
					}
					counter1--;

					int value2 = (int) (pt.getY());
					while (Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter2) < value2) {
						counter2++;
					}
					counter2--;

					// if left button released
					if (e.getButton() == MouseEvent.BUTTON1) {
						// calls the left click method
						currentlyPlacingShip = false;
						leftClick(shipNum, counter1, counter2);
						// if right button released
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						rightClick(shipNum, counter1, counter2);
					}

					endSetup.repaint();

				}

			});
		}
	}

	/*
	 * Gets called when right click is pressed on a ship panel. Attempts to
	 * rotate the panel
	 */
	private void rightClick(int shipNum, int x, int y) {
		// isVertical is set based on the layout of the panel (X or y axis)
		boolean isVertical = false;
		if (((BoxLayout) panelArray[shipNum].getLayout()).getAxis() == BoxLayout.Y_AXIS) {
			isVertical = true;
		}
		// calls the remove method to remove the ship (not the panel)
		removeShipFromGridArray(shipArray[shipNum], isVertical);
		// attempts to rotate the panel.
		if (rotatePanel(panelArray[shipNum]) && !currentlyPlacingShip) {
			// if it works call the add method to add the ship pieces in the
			// new orientation
			addShipToGridArray(shipArray[shipNum], new Point(x, y), !isVertical);
		} else if (!currentlyPlacingShip) {
			panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
			rotatePanel(panelArray[shipNum]);
		}

		showFinishButton();
	}

	/*
	 * Gets called when left mouse is pressed on a ship panel
	 */
	private void leftClick(int shipNum, int x, int y) {
		// if the panel has an X_AXIS box layout
		if ((((BoxLayout) panelArray[shipNum].getLayout()).getAxis() == BoxLayout.X_AXIS)) {
			// checks if the panel is on the grid
			if (x < gridArray.length - panelArray[shipNum].getWidth() / TILE_SIZE + 1 && x >= 0) {
				if (y < gridArray[0].length - panelArray[shipNum].getHeight() / TILE_SIZE + 1 && y >= 0) {
					// calls the method to place a ship panel on the proper
					// place on the grid image
					placeShipPanelOnGrid(x, y, shipNum, false);
				} else {
					// sets the location back to its starting position
					panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
					// removes the panel from the array
					removeShipFromGridArray(shipArray[shipNum], false);
				}
			} else {
				// sets the location back to the starting position
				panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
				// removes the panel from the array
				removeShipFromGridArray(shipArray[shipNum], false);
			}
		} else {
			// checks if the panel is on the grid
			if (x < gridArray.length - panelArray[shipNum].getWidth() / TILE_SIZE + 1 && x >= 0) {
				if (y < gridArray[0].length - panelArray[shipNum].getHeight() / TILE_SIZE + 1 && y >= 0) {
					// calls the method to place a ship panel on the proper
					// place on the grid image
					placeShipPanelOnGrid(x, y, shipNum, true);
				} else {
					// rotates the panel so it is along the x axis
					rotatePanel(panelArray[shipNum]);
					// sets the location back to the starting position
					panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
					// removes the panel from the array
					removeShipFromGridArray(shipArray[shipNum], true);
				}
			} else {
				// rotates the panel so it is along the x axis
				rotatePanel(panelArray[shipNum]);
				// sets the location back to the starting position
				panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());
				// removes the panel from the array
				removeShipFromGridArray(shipArray[shipNum], true);
			}
		}

		showFinishButton();
	}

	/*
	 * Method for placing a ship panel on the grid image
	 */
	private void placeShipPanelOnGrid(int x, int y, int shipNum, boolean isVertical) {
		// sets the location
		panelArray[shipNum].setLocation(X_ORIGIN + x + (((TILE_SIZE + BORDER_SIZE) * x) + BORDER_SIZE / 2),
				Y_ORIGIN + y + ((TILE_SIZE + BORDER_SIZE) * y) + BORDER_SIZE / 2);
		// checks for an intersection with another panel
		if (isIntersection(panelArray[shipNum])) {
			// if it is vertical
			if (isVertical) {
				// rotate the ship panel
				rotatePanel(panelArray[shipNum]);
			}
			// remove the ship from the grid array
			removeShipFromGridArray(shipArray[shipNum], false);
			// sets the panel location to its original location
			panelArray[shipNum].setLocation(shipArray[shipNum].getStartingOffGridPosition());

			// if there is no intersection
		} else {
			removeShipFromGridArray(shipArray[shipNum], isVertical);
			addShipToGridArray(shipArray[shipNum], new Point(x, y), isVertical);

		}
	}

	/*
	 * Checks if the show finish button should be added, and if it should be add
	 * it
	 */
	private void showFinishButton() {
		boolean showButton = true;
		if (!currentlyPlacingShip) {
			for (int i = 0; i < shipArray.length; i++) {
				if (shipArray[i].getStartingOffGridPosition().equals(panelArray[i].getLocation())) {
					showButton = false;
				}
			}
			endSetup.setVisible(showButton);
		}
	}

	/*
	 * Checks if a panel intersects another panel.
	 */
	private boolean isIntersection(JPanel p) {
		// loops through the panel array
		for (int i = 0; i < panelArray.length; i++) {
			// checks if p intersects with a panel in the array other than
			// itself
			if (p.getBounds().intersects(panelArray[i].getBounds()) && !p.equals(panelArray[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Removes ships from the grid array
	 */
	private void removeShipFromGridArray(Ship ship, boolean isVertical) {
		// loops through the grid array
		for (int i = 0; i < gridArray.length; i++) {
			for (int j = 0; j < gridArray[i].length; j++) {
				for (int k = 0; k < ship.getShipPieces().length; k++) {
					if (gridArray[j][i] == (ShipPiece) ship.getShipPieces()[k]) {
						gridArray[j][i] = 1;
					}
				}
			}
		}
	}

	/*
	 * Add ship to grid array
	 */
	private void addShipToGridArray(Ship ship, Point location, boolean isVertical) {

		// if the location is a valid point in the array
		if (location.getX() < gridArray.length && location.getX() >= 0 && location.getY() < gridArray.length
				&& location.getY() >= 0) {
			// loop through the ship pieces in the ship
			for (int i = 0; i < ship.getShipPieces().length; i++) {
				// if the ship is vertical
				if (isVertical) {
					// add a ship piece at the point but with i added to the y
					// coordinate
					gridArray[(int) location.getX()][(int) location.getY() + i] = ship.getShipPieces()[i];
				} else {
					// add a ship piece at the point but with i added to the x
					// coordinate
					gridArray[(int) location.getX() + i][(int) location.getY()] = ship.getShipPieces()[i];
				}
			}
		}
	}

	/*
	 * Rotates a ship panel
	 */
	private boolean rotatePanel(JPanel panel) {
		// if the panel has an x axis box layout
		if (((BoxLayout) panel.getLayout()).getAxis() == BoxLayout.X_AXIS) {
			if (panel.getX() > X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * gridArray.length) && !currentlyPlacingShip) {
				return false;
			}
			// set the layout to y axis
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			// swap the width and height
			int temp = panel.getWidth();
			int temp2 = panel.getHeight();
			panel.setSize(temp2, temp);
			// replaces all x axis padding with y axis padding between the ship
			// piece pictures
			for (int i = 0; i < panel.getComponentCount(); i++) {
				if (!panel.getComponent(i).getClass().toString().equals("JPanel")) {
					panel.add(Box.createRigidArea(new Dimension(0, BORDER_SIZE + 2)), i);
					panel.remove(++i);
				}
			}
			panel.add(Box.createRigidArea(new Dimension(0, 0)), 0);
			panel.remove(1);
			// revalidates the panel, forcing the layout to update
			panel.validate();

			// sets the panel location
			panel.setLocation(panel.getX(), panel.getY());

			// gets the length of the ship
			int counter = 0;
			while (Y_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter) < panel.getY() + panel.getWidth()) {
				counter++;
			}
			counter--;

			// if the panel is intersecting another ship panel or is partially
			// off the grid
			if (!(counter <= gridArray[0].length - panel.getHeight() / TILE_SIZE && counter >= 0)
					|| isIntersection(panel)) {
				// return that the rotation was a failure
				return false;
			}
			// if the panel has a y axis box layout
		} else if (((BoxLayout) panel.getLayout()).getAxis() == BoxLayout.Y_AXIS) {
			// set the layout to y axis
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			// swap the width and height
			int temp = panel.getWidth();
			int temp2 = panel.getHeight();
			panel.setSize(temp2, temp);
			// replaces all y axis padding with x axis padding between the ship
			// piece pictures
			for (int i = 0; i < panel.getComponentCount(); i++) {
				if (!panel.getComponent(i).getClass().toString().equals("JPanel")) {
					panel.add(Box.createRigidArea(new Dimension(BORDER_SIZE + 2, 0)), i);
					panel.remove(++i);
				}
			}
			panel.add(Box.createRigidArea(new Dimension(0, 0)), 0);
			panel.remove(1);
			// revalidates the panel, forcing the layout to update
			panel.validate();

			// sets the panel location
			panel.setLocation(panel.getX(), panel.getY());

			// gets the length of the ship
			int counter = 0;
			while (X_ORIGIN + ((TILE_SIZE + BORDER_SIZE) * counter) < panel.getX() + panel.getHeight()) {
				counter++;
			}
			counter--;

			// if the panel is intersecting another ship panel or is partially
			// off the grid
			if (!(counter <= gridArray.length - panel.getWidth() / TILE_SIZE && counter >= 0)
					|| isIntersection(panel)) {
				// set the location to the starting location
				// panel.setLocation(shipArray[shipNum].getStartingOffGridPosition());
				// return that the rotation was a failure
				return false;
			}

		}
		// return that the rotation was a success
		return true;
	}

	/*
	 * Returns the grid array
	 */
	public Object[][] getGridArray() {
		return gridArray;
	}

	/*
	 * Returns if setup is over
	 */
	public boolean isSetupOver() {
		return setupOver;
	}

	public JButton getButton() {
		return endSetup;
	}

}
