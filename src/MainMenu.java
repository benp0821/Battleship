import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainMenu implements MouseListener{
	
	private JFrame window;
	private ImageIcon backgroundImageIcon;
	private JLabel bkgImageContainer;
	private volatile boolean isImageVisible;
	
	public MainMenu(JFrame theWindow){
		window = theWindow;
		backgroundImageIcon = new ImageIcon("Title.png");
		bkgImageContainer = new JLabel(backgroundImageIcon);
		isImageVisible = true;
	}
	
	public void loadTitleScreen() {
		bkgImageContainer.setSize(window.getContentPane().getWidth(),
				window.getContentPane().getHeight());
		bkgImageContainer.setLocation(0, 0); 
		window.getContentPane().add(bkgImageContainer);
		bkgImageContainer.addMouseListener(this);
		bkgImageContainer.setVisible(true);
		window.setVisible(true);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
	}

	public boolean isImageVisible(){
		return isImageVisible;
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		window.getContentPane().remove(bkgImageContainer);
		bkgImageContainer.removeMouseListener(this);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
		isImageVisible = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}
	
}
