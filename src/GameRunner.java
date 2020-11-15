import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameRunner {
	
	JFrame jf;
	DrawPanel dp;
	
	public int w, h;
	
	private boolean setUp = false;
	
	private ChessBoard board;
	
	public static void main(String[] args) {
		new GameRunner().run();
	}
	
	private void run() {
		jf = new JFrame("Chess");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dp = new DrawPanel();
		jf.getContentPane().add(BorderLayout.CENTER, dp);
		
		jf.setSize(400, 420);
		jf.setLocation(375, 50);
		jf.setVisible(true);
		jf.setResizable(false);
		jf.addKeyListener(new KeyListen());
		jf.addMouseListener(new MouseListen());
		
		w = jf.getWidth();
		h = jf.getHeight() - 20;
		
		board = new ChessBoard(400, 400);//don't change size values
		board.setUp();
		
		setUp = true;
		
		while (true) {
			jf.repaint();
			try {
				Thread.sleep(40);
			} catch (Exception exc) {}
		}
	}
	
	public class DrawPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
			if (setUp) {
				board.runAnimate();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, w, h);
				board.draw(g);
			}
		}
	}
	
	public class KeyListen extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			board.keyPressed(e);
		}
		
		public void keyReleased(KeyEvent e) {
			board.keyReleased(e);
		}
	}
	

	public class MouseListen extends MouseAdapter {
		public void mousePressed(MouseEvent m) {
			board.mousePressed(m);
		}
	}
	
}