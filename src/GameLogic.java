import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameLogic {
	
	public static int boardSize = 10;
	
	public static int battleshipSize = 4;
	public static int cruiserSize = 3;
	public static int destroyerSize = 2;
	public static int submarineSize = 1;
	
	public static int battleshipCount = 1;
	public static int cruiserCount = 2;
	public static int destroyerCount = 3;
	public static int submarineCount = 4;
	private JFrame frame;
	private boolean gameRunning;
	
	public void setUpWindow() {
		frame = new JFrame();
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(900, 615));
		frame.setMinimumSize(new Dimension(900, 615));
		frame.setResizable(false);
		frame.pack();
		startGame();
	}
	
	public void startGame(){
		gameRunning = true;
		
		MainMenu startMenu = new MainMenu(frame);
		startMenu.loadTitleScreen();
		while(startMenu.isImageVisible()){}
		
		Ship[] p1Ships = initializeShipCreation(true);
		Ship[] p2Ships = initializeShipCreation(false);
		
		Grid grid = new Grid(chooseShipPositions(p1Ships));
		SmallGrid small = new SmallGrid(chooseShipPositions(p2Ships));
		small.setLocation(grid.getWidth()+10, grid.getY());
		
		//panel.setLayout(null);
		
		int windowWidth = small.getX() + small.getWidth() + 10;
		frame.setPreferredSize(new Dimension(windowWidth, frame.getHeight())); 
		frame.setSize(frame.getPreferredSize());
		frame.pack();
		
		frame.getContentPane().add(grid); // adds the grids to the window
		frame.getContentPane().add(small);
		frame.addMouseListener(grid);
		frame.setVisible(true);

		gameLoop(p1Ships, p2Ships, grid, small);
	}
	
	private Ship[] initializeShipCreation(boolean isPlayerOne) {
		Ship[] battleships = createShips(battleshipSize, battleshipCount, isPlayerOne);
		Ship[] cruisers = createShips(cruiserSize, cruiserCount, isPlayerOne);
		Ship[] destroyers = createShips(destroyerSize, destroyerCount, isPlayerOne);
		Ship[] submarines = createShips(submarineSize, submarineCount, isPlayerOne);

		Ship[] ships = concatShipArray(battleships, cruisers);
		ships = concatShipArray(ships, destroyers);
		ships = concatShipArray(ships, submarines);

		return ships;
	}

	private Ship[] createShips(int shipSize, int numOfShips, boolean isPlayerOne) {
		Ship[] listOfShips = new Ship[numOfShips];
		for (int i = 0; i < numOfShips; i++) {
			ShipPiece[] shipArray = new ShipPiece[shipSize];
			for (int j = 0; j < shipSize; j++) {
				ShipPiece p = new ShipPiece(isPlayerOne);
				shipArray[j] = p;
			}
			listOfShips[i] = new Ship(shipArray);
		}

		return listOfShips;
	}

	private Ship[] concatShipArray(Ship[] a, Ship[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Ship[] c = new Ship[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
	
	private Object[][] chooseShipPositions(Ship[] ships){
		GridCreator creator = new GridCreator(ships, boardSize, frame);
		creator.setup();
		frame.getContentPane().add(creator);
		frame.getContentPane().repaint();
		frame.setVisible(true);
		while (!creator.isSetupOver()) {}
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		return creator.getGridArray();
	}
	
	private void betweenTurns(Grid grid, SmallGrid small){
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				BetweenTurnsScreen betweenTurns = new BetweenTurnsScreen((JPanel) frame.getContentPane(), grid, small);
				final Object[][] grid1Temp = grid.getArray();
				final Object[][] grid2Temp = small.getArray();
				if (!grid.isTurn() && gameRunning){
					grid.setVisible(false);
					small.setVisible(false);
					grid.setArray(grid2Temp);
					small.setArray(grid1Temp); 
					betweenTurns.loadTurnScreen();
				}
			}
		});
	}
	
	private void gameLoop(Ship[] p1Ships, Ship[] p2Ships, Grid grid, SmallGrid small){
		betweenTurns(grid, small);
		
		while (gameRunning) {

			boolean p1AllShipsDead = true;

			for (int i = 0; i < p1Ships.length; i++) {
				if (p1Ships[i].checkIfDead()) {
					for (int j = 0; j < p1Ships[i].getShipPieces().length; j++)
						p1Ships[i].getShipPieces()[j].setShipImage("dead.png");
				} else {
					p1AllShipsDead = false;
				}
			}

			boolean p2AllShipsDead = true;

			grid.repaint();
			small.repaint();

			for (int i = 0; i < p2Ships.length; i++) {
				if (p2Ships[i].checkIfDead()) {
					for (int j = 0; j < p2Ships[i].getShipPieces().length; j++)
						p2Ships[i].getShipPieces()[j].setShipImage("dead.png");
				} else {
					p2AllShipsDead = false;
				}
			}

			// while(turnButton.isVisible()){}
			grid.repaint();
			small.repaint();

			if (p1AllShipsDead || p2AllShipsDead) {
				gameRunning = false;
				for (int i = 0; i < grid.getArray().length; i++) {
					for (int j = 0; j < grid.getArray()[i].length; j++) {
						if ((grid.getArray()[i][j].equals((Object) 1))) {
							grid.getArray()[i][j] = (Object) 0;
						}
					}
				}
				//grid.repaint();
				//small.repaint();
				//grid.setVisible(true); 
				GameOverScreen gameOver = new GameOverScreen(frame, !p1AllShipsDead);
				gameOver.loadEndScreen();
			}
		}
		
	}
}