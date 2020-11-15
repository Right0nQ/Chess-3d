import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Queen extends ChessPiece{
	
	private static int baseY;
	
	public static BufferedImage whitePiece;
	public static BufferedImage blackPiece;
	public static boolean setPicYet = false;
	
	public Queen(int x, int y, boolean white) {
		super(x, y, 5 + (white? 0: 6), 9, white, -1);
	}
	
	public Queen(int x, int y, boolean white, int index) {
		super(x, y, 5 + (white? 0: 6), 9, white, index);
	}
	
	public void setPic() {
		if (!setPicYet) {
			File f = new File("images/white_queen.png");
			try {
				whitePiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			f = new File("images/black_queen.png");
			try {
				blackPiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			baseY = whitePiece.getHeight() - 7;//assumes both colors same dimensions
			setPicYet = true;
		}
	}
	
	public void draw(Graphics g) {
		int width = (int) ((white? whitePiece: blackPiece).getWidth() / drawZ);
		int height = (int) ((white? whitePiece: blackPiece).getHeight() / drawZ);
		g.drawImage((white? whitePiece: blackPiece), (int) drawX - width / 2, (int) drawY - (int) (baseY * ((double) height / whitePiece.getHeight())), width, height, null);
	}
	
	public ChessPiece copy(int index) {
		ChessPiece hold = new Queen(x, y, white, index);
		hold.ID = ID;
		totalPieces--;
		hold.moved = moved;
		hold.movedTwo = movedTwo;
		return hold;
	}

	public ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces) {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		moves = new Bishop(x, y, white, true).getMoves(kx, ky, board, pieces);
		moves.addAll(new Rook(x, y, white, true).getMoves(kx, ky, board, pieces));
		
		filterMoves(kx, ky, board, pieces, moves, false);
		
		return moves;
	}
	
	public boolean checking(int kx, int ky, int[][] board) {
		return (new Bishop(x, y, white, true).checking(kx, ky, board)) || (new Rook(x, y, white, true).checking(kx, ky, board));
	}
}