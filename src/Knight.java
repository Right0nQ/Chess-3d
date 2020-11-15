import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Knight extends ChessPiece{
	
	private static int baseY;
	
	public static BufferedImage whitePiece;
	public static BufferedImage blackPiece;
	public static boolean setPicYet = false;
	
	//possible moves
	private final static int[] pMoves = {1, 2, 2, 1, -1, 2, -2, 1, 1, -2, 2, -1, -1, -2, -2, -1};//couldn't think of a simple way to do this
	
	public Knight(int x, int y, boolean white) {
		super(x, y, 3 + (white? 0: 6), 3, white, -1);
	}
	
	public Knight(int x, int y, boolean white, int index) {
		super(x, y, 3 + (white? 0: 6), 3, white, index);
	}
	
	public void setPic() {
		if (!setPicYet) {
			File f = new File("images/white_knight.png");
			try {
				whitePiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			f = new File("images/black_knight.png");
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
		ChessPiece hold = new Knight(x, y, white, index);
		hold.ID = ID;
		totalPieces--;
		hold.moved = moved;
		hold.movedTwo = movedTwo;
		return hold;
	}

	public ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces) {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int i = 0; i < pMoves.length; i += 2) {
			if (x + pMoves[i] >= 0 && x + pMoves[i] < 8 && y + pMoves[i+1] >= 0 && y + pMoves[i+1] < 8) {
				if (board[y + pMoves[i+1]][x + pMoves[i]] == 0 || (white? board[y + pMoves[i+1]][x + pMoves[i]] >= 7: (board[y + pMoves[i+1]][x + pMoves[i]] > 0 && board[y + pMoves[i+1]][x + pMoves[i]] < 7))) {
					moves.add(x + pMoves[i]);
					moves.add(y + pMoves[i + 1]);
				}
			}
		}
		
		filterMoves(kx, ky, board, pieces, moves, false);
		
		return moves;
	}
	
	public boolean checking(int kx, int ky, int[][] board) {
		for (int i = 0; i < pMoves.length; i += 2) {
			if (x + pMoves[i] >= 0 && x + pMoves[i] < 8 && y + pMoves[i+1] >= 0 && y + pMoves[i+1] < 8) {
				if (y + pMoves[i+1] == ky && x + pMoves[i] == kx)
					return true;
			}
		}
		return false;
	}
}